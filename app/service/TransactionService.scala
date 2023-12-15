package service

import java.time.LocalDateTime
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

import com.google.inject.Singleton
import controller.model.TransactionDetail
import repository.AccountRepository
import repository.TransactionRepository
import service.model.Transaction
import service.model.TransactionServiceStatus
import service.model.TransactionServiceStatus.ACCOUNTNOTFOUND
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
  ): Future[Either[TransactionServiceStatus, TransactionDetail]] =
    accountRepository
      .findById(accountId)
      .flatMap {
        case Some(account) =>
          transactionRepository
            .findByAccountId(account.accountId)
            .map(transactions =>
              Right(
                TransactionDetail(
                  Random.nextInt(9999999),
                  accountId,
                  amount,
                  description,
                  TransactionStatus.COMPLETED,
                  LocalDateTime.now()
                )
              )
            )
        case _ => Future.successful(Left(ACCOUNTNOTFOUND))
      }

  override def getTransactionById(transactionId: Long): Future[Either[TransactionServiceStatus, TransactionDetail]] = {
    transactionRepository
      .findById(transactionId)
      .map {
        case Some(tx) =>
          Right(
            TransactionDetail(
              transactionId = transactionId,
              accountId = tx.accountId,
              amount = tx.amount,
              description = tx.description,
              status = TransactionStatus.withName(tx.status),
              date = tx.created.toLocalDateTime
            )
          )
        case _ => Left(ACCOUNTNOTFOUND)
      }
  }

  /*private def totalAmount(records: List[LoanRecord]): Int =
    records.foldLeft(0)((acumm, loan) => acumm + loan.amount)

  private def totalCount(records: List[LoanRecord]): Int = records.size*/

}
