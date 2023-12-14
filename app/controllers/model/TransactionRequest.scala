package controllers.model

import play.api.libs.json.Json
import play.api.libs.json.Reads

case class TransactionRequest(
    accountId: String,
    amount: Double,
    description: String
)

object TransactionRequest {
  implicit val TransactionRequestFormat: Reads[TransactionRequest] = Json.reads[TransactionRequest]

}
