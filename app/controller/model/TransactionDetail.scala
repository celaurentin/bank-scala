package controller.model

import java.time.LocalDateTime

import play.api.libs.json.Json
import play.api.libs.json.OFormat
import service.model.TransactionStatus

case class TransactionDetail(
    transactionId: Long,
    accountId: String,
    amount: Double,
    description: String,
    status: TransactionStatus,
    date: LocalDateTime
)

object TransactionDetail {
  implicit val TransactionDetailFormat: OFormat[TransactionDetail] = Json.format[TransactionDetail]

}
