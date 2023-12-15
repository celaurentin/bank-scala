package service.model

import java.sql.Timestamp

case class Transaction(
    transactionId: Long,
    accountId: String,
    amount: Double,
    description: String,
    status: String,
    created: Timestamp
)
