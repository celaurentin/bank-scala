package controllers.model

import play.api.libs.json._
import service.model.AccountStatus

case class AccountDetail(
    accountId: Long,
    balance: Double,
    status: AccountStatus,
    user: User
)

case class User(
    userId: Long,
    name: String,
    lastName: String
)

object AccountDetail {
  implicit val AccountDetailFormat: OWrites[AccountDetail] = Json.writes[AccountDetail]

}

object User {
  implicit val UserFormat: OWrites[User] = Json.writes[User]

}
