package service.model

import java.time.LocalDate

case class User(
    userId: Long,
    firstName: String,
    lastName: String,
    dateOfBirth: LocalDate,
    email: Option[String],
    addressLine1: String,
    addressLine2: Option[String],
    city: String,
    state: String,
    zipCode: String
)
