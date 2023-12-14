package service.model

case class Account(
    accountId: String,
    userId: Long,
    balance: Double,
    status: AccountStatus
)
