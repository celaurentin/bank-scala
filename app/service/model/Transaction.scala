package service.model

import java.time.LocalDate

case class Transaction(
    transactionId: Long,
    accountId: Long,
    amount: Double,
    description: String,
    status: TransactionStatus,
    timestamp: LocalDate
)
