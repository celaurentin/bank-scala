package controller.model

import play.api.libs.json.Json
import play.api.libs.json.Reads

/**
 * @param description In some cases it will contains a keyword 'PND' to flag whether it need sit
 *                    as a [[service.model.TransactionStatus.PENDING]] transaction or not
 */
case class TransactionRequest(
    accountId: String,
    amount: Double,
    description: String
)

object TransactionRequest {
  implicit val TransactionRequestFormat: Reads[TransactionRequest] = Json.reads[TransactionRequest]

}
