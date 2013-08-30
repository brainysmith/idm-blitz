package services.login

object AuthenticationMethods extends Enumeration {
  type AuthenticationMethods = Value

  //the identifier must be power of two
  val BASIC = Value(1)
  val OTP = Value(2)
  val SMART_CARD = Value(4)
}
