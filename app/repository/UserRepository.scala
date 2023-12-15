package repository

import javax.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import service.model.User
import slick.jdbc.H2Profile

class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext
) extends HasDatabaseConfigProvider[H2Profile] {
  import profile.api._

  private val users = TableQuery[UserTable]

  def findById(id: Long): Future[Option[User]] =
    db.run(users.filter(_.userId === id).result.headOption)

  private class UserTable(tag: Tag) extends Table[User](tag, "BANK_USER") {

    def * = (userId, firstName, lastName, city, state, zipCode) <> (User.tupled, User.unapply)

    def userId = column[Long]("USER_ID", O.PrimaryKey)

    def firstName = column[String]("FIRST_NAME")

    def lastName = column[String]("LAST_NAME")

    def city = column[String]("CITY")

    def state = column[String]("STATE")

    def zipCode = column[String]("ZIP_CODE")
  }

}
