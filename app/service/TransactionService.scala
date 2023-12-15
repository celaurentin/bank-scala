package service

import java.time.LocalDateTime
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

import com.google.inject.Singleton
import controller.model.TransactionDetail
import repository.TransactionRepository
import service.model.TransactionStatus

trait TransactionService {
  def getHistory(
      accountId: String
  ): Future[Either[String, List[TransactionDetail]]]

  def getTransactionById(
      transactionId: Long
  ): Future[Either[String, TransactionDetail]]

  def createTransaction(
      accountId: String,
      amount: Double,
      description: String
  ): Future[TransactionDetail]
}

@Singleton
class TransactionServiceImpl @Inject() (transactionRepository: TransactionRepository, implicit val ec: ExecutionContext)
    extends TransactionService {

  override def getHistory(
      accountId: String
  ): Future[Either[String, List[TransactionDetail]]] = Future {
    Right(
      List(
        TransactionDetail(
          Random.nextInt(9999999),
          accountId,
          35.01,
          "POS:DESC: Target",
          TransactionStatus.COMPLETED,
          LocalDateTime.now()
        )
      )
    )
  }

  /*private def totalAmount(records: List[LoanRecord]): Int =
    records.foldLeft(0)((acumm, loan) => acumm + loan.amount)

  private def totalCount(records: List[LoanRecord]): Int = records.size*/

  override def createTransaction(accountId: String, amount: Double, description: String): Future[TransactionDetail] =
    Future {
      TransactionDetail(
        Random.nextInt(9999999),
        accountId,
        amount,
        description,
        TransactionStatus.COMPLETED,
        LocalDateTime.now()
      )
    }

  override def getTransactionById(transactionId: Long): Future[Either[String, TransactionDetail]] = {
    transactionRepository
      .findById(transactionId)
      .map {
        case Some(t) =>
          Right(
            TransactionDetail(
              transactionId = transactionId,
              accountId = t.accountId,
              amount = t.amount,
              description = t.description,
              status = TransactionStatus.withName(t.status),
              date = t.created.toLocalDateTime
            )
          )
        case _ => Left("Transaction not found")
      }
  }
}
