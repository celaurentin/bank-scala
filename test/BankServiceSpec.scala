import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Future

import controllers.model.DataPoint
import controllers.model.LoanReport
import model._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import service.AccountServiceImpl
import service.TransactionServiceImpl
import BankServiceSpec.Fixture

class BankServiceSpec extends AnyFunSpec with Matchers with MockitoSugar with Fixture {

  implicit val ec: ExecutionContextExecutor            = scala.concurrent.ExecutionContext.global
  val loanDataAccessService: AccountServiceImpl = mock[AccountServiceImpl]

  val loanService = new TransactionServiceImpl(loanDataAccessService, ec)

  describe("getLoansReport") {
    describe("Amount") {
      it("should be a valid total loans by grade report") {
        when(loanDataAccessService.getAccountById(any())).thenReturn(Future.successful(allRecords))
        val result = loanService.getTransactions(
          accountId = ReportType.Amount.entryName,
          groupingKey = ReportFilter.Grade.entryName
        )
        result.futureValue shouldBe loanReport1
      }
      it("should be a valid total loans by state report") {
        when(loanDataAccessService.getAccountById(any())).thenReturn(Future.successful(allRecords))
        val result = loanService.getTransactions(
          accountId = ReportType.Amount.entryName,
          groupingKey = ReportFilter.State.entryName
        )
        result.futureValue shouldBe loanReport2
      }
    }

    describe("Count") {
      it("should be a valid total loans by grade report") {
        when(loanDataAccessService.getAccountById(any())).thenReturn(Future.successful(allRecords))
        val result = loanService.getTransactions(
          accountId = ReportType.Count.entryName,
          groupingKey = ReportFilter.Grade.entryName
        )
        result.futureValue shouldBe loanReport3
      }
      it("should be a valid total loans by state report") {
        when(loanDataAccessService.getAccountById(any())).thenReturn(Future.successful(allRecords))
        val result = loanService.getTransactions(
          accountId = ReportType.Count.entryName,
          groupingKey = ReportFilter.State.entryName
        )
        result.futureValue shouldBe loanReport4
      }
    }
  }

}

object BankServiceSpec {

  trait Fixture {
    val loanRecord1 = LoanRecord(
      id = 1,
      amount = 50,
      term = " 36 months",
      grade = TransactionStatus.A,
      jobTitle = "Manager",
      issueDate = "Nov-2017",
      status = LoanStatus.Current,
      state = "CA",
      purpose = LoanPurpose.DebtConsolidation,
      ficoRange = FicoRange(705, 709)
    )

    val loanRecord2 = loanRecord1.copy(id = 2, amount = 200, state = "IL", purpose = LoanPurpose.CreditCard)
    val loanRecord3 = loanRecord1.copy(id = 3, amount = 300, grade = TransactionStatus.B)
    val allRecords  = List(loanRecord1, loanRecord2, loanRecord3)

    // By Grade
    val dataPoint1 = DataPoint("B", "300")
    val dataPoint2 = DataPoint("A", "250")

    // By State
    val dataPoint3 = DataPoint("CA", "350")
    val dataPoint4 = DataPoint("IL", "200")

    val loanReport1 = LoanReport(
      title = "Total Loans by Grade",
      xLabel = "Grades",
      yLabel = "Total Loans - Millions",
      data = List(dataPoint1, dataPoint2)
    )

    val loanReport2 = LoanReport(
      title = "Total Loans by State",
      xLabel = "States",
      yLabel = "Total Loans - Millions",
      data = List(dataPoint3, dataPoint4)
    )

    val loanReport3 = loanReport1.copy(
      yLabel = "Total Loans - Count",
      data = List(dataPoint2.copy(value = "2"), dataPoint1.copy(value = "1"))
    )

    val loanReport4 = loanReport2.copy(
      yLabel = "Total Loans - Count",
      data = List(dataPoint3.copy(value = "2"), dataPoint4.copy(value = "1"))
    )
  }

}
