package controllers

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import controllers.model.TransactionRequest
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import service.TransactionServiceImpl

class TransactionController @Inject() (
    cc: ControllerComponents,
    transactionService: TransactionServiceImpl
)(
    implicit assetsFinder: AssetsFinder,
    implicit val ec: ExecutionContext
) extends AbstractController(cc) {

  def getHistory(accountId: String): Action[AnyContent] = Action.async {
    transactionService
      .getHistory(
        accountId
      )
      .map(r => Ok(Json.toJson(r)))
  }

  def create: Action[TransactionRequest] = Action.async(parse.json[TransactionRequest]) { implicit request =>
    transactionService
      .createTransaction(
        request.body.accountId,
        request.body.amount,
        request.body.description
      )
      .map(r => Ok(Json.toJson(r)))

  }

}
