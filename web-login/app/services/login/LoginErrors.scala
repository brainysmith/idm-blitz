package services.login

object LoginErrors extends Enumeration {
  type LoginErrors = Value

  val NO_USER_FOUND,
      INVALID_CREDENTIALS,
      ACCOUNT_IS_LOCKED = Value
}
