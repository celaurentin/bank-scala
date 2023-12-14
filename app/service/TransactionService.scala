package service

import java.time.LocalDate
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Random

import com.google.inject.Singleton
import controller.model.TransactionDetail
import service.model.TransactionStatus

trait TransactionService {
  def getHistory(
      accountId: String
  ): Future[List[TransactionDetail]]

  def createTransaction(
      accountId: String,
      amount: Double,
      description: String
  ): Future[TransactionDetail]
}

@Singleton
class TransactionServiceImpl @Inject() (accountService: AccountServiceImpl, implicit val ec: ExecutionContext)
    extends TransactionService {

  override def getHistory(
      accountId: String
  ): Future[List[TransactionDetail]] = Future {
    List(
      TransactionDetail(
        Random.nextInt(9999999),
        accountId,
        35.01,
        "POS:DESC: Target",
        TransactionStatus.COMPLETED,
        LocalDate.now()
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
        LocalDate.now()
      )
    }
}
