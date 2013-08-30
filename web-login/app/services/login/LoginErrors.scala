package services.login

object LoginErrors extends Enumeration {
  type LoginErrors = Value

  val INTERNAL,
      PRE_AUTHENTICATION_REQUIRED,
      NO_CREDENTIALS_FOUND,
      NO_SUBJECT_FOUND,
      INVALID_CREDENTIALS,
      ACCOUNT_IS_LOCKED,
      WRONG_OLD_PASSWORD,
      INAPPROPRIATE_NEW_PASSWORD = Value

  class LoginErrorsVal(loginError: Value)  {
    def getKey: String = this.getClass.getSimpleName + "." + loginError.toString
  }

  implicit def valueToLoginErrors(loginError: Value) = new LoginErrorsVal(loginError)
}
