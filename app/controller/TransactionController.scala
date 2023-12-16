package controller

import javax.inject.Inject

import scala.concurrent.ExecutionContext

import controller.model.TransactionRequest
import controllers.AssetsFinder
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import service.model.TransactionServiceStatus
import service.model.TransactionServiceStatus.ACCOUNTNOTFOUND
import service.model.TransactionServiceStatus.INSUFFICIENTFUNDS
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
      .map {
        case Right(transactions) => Ok(Json.toJson(transactions))
        case Left(_)             => NotFound
      }
  }

  def create: Action[TransactionRequest] = Action.async(parse.json[TransactionRequest]) { implicit request =>
    transactionService
      .createTransaction(
        request.body.accountId,
        request.body.amount,
        request.body.description
      )
      .map {
        case Right(result)      => Created(Json.toJson(result))
        case Left(failedReason) => convert(failedReason)
      }

  }

  private def convert(failedReason: TransactionServiceStatus): Status = failedReason match {
    case ACCOUNTNOTFOUND   => NotFound
    case INSUFFICIENTFUNDS => UnprocessableEntity
    case _                 => UnprocessableEntity
  }

}
