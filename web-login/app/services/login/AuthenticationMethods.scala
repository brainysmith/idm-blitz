package services.login

object AuthenticationMethods extends Enumeration {
  type LoginStatuses = Value

  val INITIAL = Value(-1)
  val SUCCESS = Value(0)
  val FAIL = Value(1)
  val PROCESSING = Value(2)
}
