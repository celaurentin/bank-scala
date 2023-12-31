package service

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import controller.model.AccountDetail
import controller.model.UserDetail
import repository.AccountRepository
import repository.UserRepository
import service.model.Account
import service.model.AccountStatus
import service.model.User

trait AccountService {

  def getAccountById(accountId: String): Future[Either[String, AccountDetail]]
}

@Singleton
class AccountServiceImpl @Inject() (
    accountRepository: AccountRepository,
    userRepository: UserRepository,
    implicit val ec: ExecutionContext
) extends AccountService {

  override def getAccountById(accountId: String): Future[Either[String, AccountDetail]] = {
    accountRepository
      .findById(accountId)
      .flatMap {
        case Some(account) =>
          userRepository
            .findById(account.userId)
            .map {
              case Some(user) => Right(convert(account, user))
              case _          => Left("User not found")
            }
        case _ => Future.successful(Left("Account not found"))
      }
  }

  private def convert(acc: Account, user: User): AccountDetail = AccountDetail(
    accountId = acc.accountId,
    balance = acc.balance,
    status = AccountStatus.withName(acc.status),
    user = UserDetail(user.userId, user.firstName, user.lastName)
  )
}
