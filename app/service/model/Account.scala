package service.model

case class Account(
    accountId: Long,
    userId: Long,
    balance: Double,
    status: AccountStatus
)
