package services.login

import com.blitz.idm.app.CustomEnumeration

sealed abstract class LoginStatus(private val _name: String) extends LoginStatus.Val {
  def name = _name
}

/**
 * Enumeration of the login statuses.
 */
object LoginStatus extends CustomEnumeration[LoginStatus] {
  case object INITIAL extends LoginStatus("initial")
  case object SUCCESS extends LoginStatus("success")
  case object FAIL extends LoginStatus("fail")
  case object PROCESSING extends LoginStatus("processing")
}