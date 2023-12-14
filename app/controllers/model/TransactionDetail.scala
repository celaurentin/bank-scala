package controllers.model

import java.time.LocalDate

import play.api.libs.json.Json
import play.api.libs.json.OWrites
import service.model.TransactionStatus

case class TransactionDetail(
    transactionId: Long,
    accountId: Long,
    amount: Double,
    description: String,
    status: TransactionStatus,
    date: LocalDate
)

object TransactionDetail {
  implicit val TransactionDetailFormat: OWrites[TransactionDetail] = Json.writes[TransactionDetail]

}
