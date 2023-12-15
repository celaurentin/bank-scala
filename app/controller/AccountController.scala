package controller

import javax.inject._

import scala.concurrent.ExecutionContext

import controllers.AssetsFinder
import play.api.libs.json.Json
import play.api.mvc._
import service.AccountServiceImpl

/**
 * This controller creates an async `Action` to handle HTTP requests to the
 * Account endpoints.
 */
@Singleton
class AccountController @Inject() (
    cc: ControllerComponents,
    accountService: AccountServiceImpl
)(
    implicit assetsFinder: AssetsFinder,
    implicit val ec: ExecutionContext
) extends AbstractController(cc) {

  def getAccount(accountId: String): Action[AnyContent] = Action.async {
    accountService
      .getAccountById(
        accountId
      )
      .map {
        case Right(r) => Ok(Json.toJson(r))
        case Left(_)  => NotFound
      }
  }

}
