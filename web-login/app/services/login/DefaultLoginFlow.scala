package services.login

import play.api.mvc.{Call, AnyContent, Request}

/**
 */
object DefaultLoginFlow extends LoginFlow {

  //todo: realise it
  def getNextPoint(implicit lc: LoginContext, request: Request[AnyContent]): Option[Call] = {
    throw new UnsupportedOperationException("Hasn't realized yet.")
  }
}
