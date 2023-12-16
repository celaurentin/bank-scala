package repository

import java.sql.Timestamp
import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import service.model.Transaction
import service.model.TransactionStatus
import slick.jdbc.H2Profile

class TransactionRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext
) extends HasDatabaseConfigProvider[H2Profile] {
  import profile.api._

  private val transactions = TableQuery[TransactionTable]

  def findById(id: Long): Future[Option[Transaction]] =
    db.run(transactions.filter(_.transactionId === id).result.headOption)

  def findByAccountId(accountId: String): Future[Seq[Transaction]] = {
    db.run[Seq[Transaction]](transactions.filter(_.accountId === accountId).result)
  }

  def create(accountId: String, amount: Double, description: String, status: TransactionStatus): Future[Transaction] = {
    val insertTransaction = transactions
      .returning(transactions.map(_.transactionId))
      .into((tx, id) => tx.copy(transactionId = id))

    val action = insertTransaction +=
    Transaction(
      0,
      accountId = accountId,
      amount = amount,
      description = description,
      status = status.entryName,
      created = new java.sql.Timestamp(System.currentTimeMillis())
    )

    db.run(action)
  }

  private class TransactionTable(tag: Tag) extends Table[Transaction](tag, "BANK_TRANSACTION") {

    def * =
      (transactionId, accountId, amount, description, status, created) <> (Transaction.tupled, Transaction.unapply)

    def transactionId = column[Long]("TRANSACTION_ID", O.PrimaryKey, O.AutoInc)

    def accountId = column[String]("ACCOUNT_ID")

    def amount = column[Double]("AMOUNT")

    def description = column[String]("DESCRIPTION")

    def status = column[String]("TRANSACTION_STATUS")

    def created = column[Timestamp]("CREATED", O.SqlType("timestamp default now()"))

  }

}
