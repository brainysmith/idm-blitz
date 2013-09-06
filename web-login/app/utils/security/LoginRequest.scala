package utils.security

import play.api.mvc.{WrappedRequest, Request}
import services.login.LoginContext

/**
 */
case class LoginRequest[A] (private val request: Request[A],
                            private val lc: LoginContext) extends WrappedRequest(request) {
  def getLoginContext = lc
}
