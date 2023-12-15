package controller.model

import play.api.libs.json._
import service.model.AccountStatus

case class AccountDetail(
    accountId: String,
    balance: Double,
    status: AccountStatus,
    user: UserDetail
)

case class UserDetail(
    userId: Long,
    firstName: String,
    lastName: String
)

object AccountDetail {
  implicit val AccountDetailFormat: OWrites[AccountDetail] = Json.writes[AccountDetail]

}

object UserDetail {
  implicit val UserFormat: OWrites[UserDetail] = Json.writes[UserDetail]

}
