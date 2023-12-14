package controllers

import javax.inject._

import scala.concurrent.ExecutionContext

import play.api.libs.json.Json
import play.api.mvc._
import service.AccountServiceImpl

/**
 * This controller creates an async `Action` to handle HTTP requests to the
 * application endpoints.
 */
@Singleton
class BankController @Inject() (cc: ControllerComponents, accountService: AccountServiceImpl)(
    implicit assetsFinder: AssetsFinder,
    implicit val ec: ExecutionContext
) extends AbstractController(cc) {

  def getAccount(accountId: String): Action[AnyContent] = Action.async {
    accountService
      .getAccountById(
        accountId
      )
      .map(r => Ok(Json.toJson(r)))
  }

}
