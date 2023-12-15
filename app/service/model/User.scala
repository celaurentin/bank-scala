package service.model

case class User(
    userId: Long,
    firstName: String,
    lastName: String,
    city: String,
    state: String,
    zipCode: String
)
