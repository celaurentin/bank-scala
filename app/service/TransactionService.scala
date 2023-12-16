package service

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.google.inject.Singleton
import controller.model.TransactionDetail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import repository.AccountRepository
import repository.TransactionRepository
import service.model.Transaction
import service.model.TransactionServiceStatus
import service.model.TransactionServiceStatus.ACCOUNTNOTFOUND
import service.model.TransactionServiceStatus.INSUFFICIENTFUNDS
import service.model.TransactionStatus
import slick.jdbc.H2Profile

trait TransactionService {
  def getHistory(
      accountId: String
  ): Future[Either[TransactionServiceStatus, List[TransactionDetail]]]

  def createTransaction(
      accountId: String,
      amount: Double,
      description: String
  ): Future[Either[TransactionServiceStatus, TransactionDetail]]
}

@Singleton
class TransactionServiceImpl @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    implicit val ec: ExecutionContext
) extends TransactionService
    with HasDatabaseConfigProvider[H2Profile] {
  import profile.api._

  val logger: Logger = LoggerFactory.getLogger("TransactionServiceImpl")

  override def getHistory(
      accountId: String
  ): Future[Either[TransactionServiceStatus, List[TransactionDetail]]] =
    accountRepository
      .findById(accountId)
      .flatMap {
        case Some(account) =>
          transactionRepository
            .findByAccountId(account.accountId)
            .map(transactions => Right(transactions.map(convert).toList.sortBy(_.date).reverse))
        case _ => Future.successful(Left(ACCOUNTNOTFOUND))
      }

  override def createTransaction(
      accountId: String,
      amount: Double,
      description: String
  ): Future[Either[TransactionServiceStatus, TransactionDetail]] = {
    accountRepository
      .findById(accountId)
      .flatMap {
        case Some(account) =>
          transactionRepository
            .findByAccountId(account.accountId)
            .flatMap(transactions =>
              if (hasEnoughFunds(account.balance, amount, transactions)) {
                val finalBalance = safeSubtraction(account.balance, amount)
                val dbAction = (for {
                  savedTransaction <- transactionRepository.create(accountId, amount, description, convert(description))
                  updatedBalance   <- accountRepository.updateBalance(accountId, finalBalance)
                } yield (savedTransaction, updatedBalance)).transactionally
                db.run(dbAction).map(result => Right(convert(result._1)))
              } else
                Future.successful(Left(INSUFFICIENTFUNDS))
            )
        case _ => Future.successful(Left(ACCOUNTNOTFOUND))
      }
  }

  private def convert(tx: Transaction): TransactionDetail = TransactionDetail(
    transactionId = tx.transactionId,
    accountId = tx.accountId,
    amount = tx.amount,
    description = tx.description,
    status = TransactionStatus.withName(tx.status),
    date = tx.created.toLocalDateTime
  )

  private def convert(description: String): TransactionStatus =
    if (description.toUpperCase.contains("PND")) TransactionStatus.PENDING else TransactionStatus.COMPLETED

  /**
   *  Method to validate funds for debit operations, it takes in account pending transactions and
   *  the operation's amount
   *
   * @param amount It could be negative (debit) or positive (credit) operation
   * @param transactions Transactions including those in [[TransactionStatus.PENDING]] status
   */
  private def hasEnoughFunds(balance: Double, amount: Double, transactions: Seq[Transaction]): Boolean = {
    val isDebit = amount < 0

    logger.debug(s"Initial Balance: $balance, Operation Amount: $amount")
    if (isDebit) {
      val totalPendingAmount = transactions
        .filter(_.status == TransactionStatus.PENDING.entryName)
        .foldLeft(0.0)((acumm, transaction) => acumm + transaction.amount)

      val actualAvailability    = safeSubtraction(balance, totalPendingAmount)
      val balanceAfterOperation = safeSubtraction(actualAvailability, amount)

      logger.debug(
        s"Pending amount: $totalPendingAmount, Actual funds:$actualAvailability, After operation:$balanceAfterOperation"
      )
      balanceAfterOperation > 0
    } else
      // Nothing to validate
      true
  }

  private def safeSubtraction(a: Double, b: Double): Double = a - (-1 * b)

}
