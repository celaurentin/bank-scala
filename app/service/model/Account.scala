package service.model

case class Account(
    accountId: String,
    userId: String,
    balance: Double,
    status: AccountStatus
)
