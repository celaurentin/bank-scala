package service

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import controllers.model.AccountDetail
import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait AccountService {

  def getAccountById(accountId: String): Future[AccountDetail]
}

@Singleton
class AccountServiceImpl @Inject() (implicit val ec: ExecutionContext) extends AccountService {

  val logger: Logger = LoggerFactory.getLogger("AccountServiceImpl")

  override def getAccountById(filePath: String): Future[AccountDetail] = ???
}
