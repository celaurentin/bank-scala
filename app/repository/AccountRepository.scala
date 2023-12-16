package repository

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import service.model.Account
import slick.jdbc.H2Profile

class AccountRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext
) extends HasDatabaseConfigProvider[H2Profile] {
  import profile.api._

  private val accounts = TableQuery[AccountTable]

  def findById(accountId: String): Future[Option[Account]] =
    db.run(accounts.filter(_.accountId === accountId).result.headOption)

  def updateBalance(accountId: String, amount: Double): DBIO[Int] =
    accounts.filter(_.accountId === accountId).map(_.balance).update(amount).transactionally

  private class AccountTable(tag: Tag) extends Table[Account](tag, "BANK_ACCOUNT") {

    def * = (accountId, userId, balance, status) <> (Account.tupled, Account.unapply)

    def accountId = column[String]("ACCOUNT_ID", O.PrimaryKey)

    def userId = column[Long]("USER_ID")

    def balance = column[Double]("ACCOUNT_BALANCE")

    def status = column[String]("ACCOUNT_STATUS")
  }

}
