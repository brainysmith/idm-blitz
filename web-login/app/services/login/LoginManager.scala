package services.login

import play.api.mvc._

/**
  */
object loginManager {

  def apply(success: Request[AnyContent] => Result)(fail: Request[AnyContent] => Result)
                  (implicit lc: LoginContext, request: Request[AnyContent]) : Result = {
    success(request)
  }

}
