package service

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.google.inject.Singleton
import controller.model.TransactionDetail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import repository.AccountRepository
import repository.TransactionRepository
import service.model.Transaction
import service.model.TransactionServiceStatus
import service.model.TransactionServiceStatus.ACCOUNTNOTFOUND
import service.model.TransactionServiceStatus.UNPROCESSABLEENTITY
import service.model.TransactionStatus

trait TransactionService {
  def getHistory(
      accountId: String
  ): Future[Either[TransactionServiceStatus, List[TransactionDetail]]]

  def getTransactionById(
      transactionId: Long
  ): Future[Either[TransactionServiceStatus, TransactionDetail]]

  def createTransaction(
      accountId: String,
      amount: Double,
      description: String
  ): Future[Either[TransactionServiceStatus, TransactionDetail]]
}

@Singleton
class TransactionServiceImpl @Inject() (
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    implicit val ec: ExecutionContext
) extends TransactionService {

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
            .map(transactions => Right(convert(transactions).sortBy(_.date).reverse))
        case _ => Future.successful(Left(ACCOUNTNOTFOUND))
      }

  private def convert(transactions: Seq[Transaction]): List[TransactionDetail] = {
    transactions
      .map(t =>
        TransactionDetail(
          transactionId = t.transactionId,
          accountId = t.accountId,
          amount = t.amount,
          description = t.description,
          status = TransactionStatus.withName(t.status),
          date = t.created.toLocalDateTime
        )
      )
      .toList
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
              if (hasEnoughFunds(account.balance, amount, transactions))
                transactionRepository
                  .create(accountId, amount, description, convert(description))
                  .map { tx =>
                    Right(convert(tx))
                  }
              else
                Future.successful(Left(UNPROCESSABLEENTITY))
            )
        case _ => Future.successful(Left(ACCOUNTNOTFOUND))
      }
  }

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

      val actualAvailability    = balance - (-1 * totalPendingAmount)
      val balanceAfterOperation = actualAvailability - (-1 * amount)

      logger.debug(
        s"Pending amount: $totalPendingAmount, Actual funds:$actualAvailability, After operation:$balanceAfterOperation"
      )
      balanceAfterOperation > 0
    } else
      // Nothing to validate
      true
  }

  override def getTransactionById(transactionId: Long): Future[Either[TransactionServiceStatus, TransactionDetail]] = {
    transactionRepository
      .findById(transactionId)
      .map {
        case Some(tx) =>
          Right(
            convert(tx)
          )
        case _ => Left(ACCOUNTNOTFOUND)
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

  // private def totalCount(transactions: Seq[Transactions]): Int = transactions.size

}
