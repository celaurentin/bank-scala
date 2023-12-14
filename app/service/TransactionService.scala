package service

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import com.google.inject.Singleton
import controllers.model.TransactionDetail


trait TransactionService {
  def getTransactions(
      accountId: String
  ): Future[TransactionDetail]
}

@Singleton
class TransactionServiceImpl @Inject()(accountService: AccountServiceImpl, implicit val ec: ExecutionContext)
    extends TransactionService {

  override def getTransactions(
      accountId: String
  ): Future[TransactionDetail] = ???

  /*private def totalAmount(records: List[LoanRecord]): Int =
    records.foldLeft(0)((acumm, loan) => acumm + loan.amount)

  private def totalCount(records: List[LoanRecord]): Int = records.size*/

}
