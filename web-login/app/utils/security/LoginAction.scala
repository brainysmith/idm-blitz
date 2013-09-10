package utils.security

import com.blitz.idm.app._
import play.api.mvc._
import services.login.LoginContextImpl

/**
 */


trait LoginActionBuilder extends Results {

  val LC_KEY_NAME = "lc"

  def apply[A](bodyParser: BodyParser[A])(a: LoginRequest[A] => Result) = {
    Action(bodyParser) { implicit request =>
      implicit val lc: LoginContextImpl = request.session.get(LC_KEY_NAME).map(lcStr => {
        appLogTrace("got the following login context from the current HTTP session: {}", lcStr)
        LoginContextImpl.fromJson(lcStr)
      }).getOrElse({
        appLogTrace("can't find the login context in the current HTTP session: make a new")
        new LoginContextImpl()
      })

      a(LoginRequest(request, lc)).withSession(LC_KEY_NAME -> lc.json.toJson)
    }
  }

  def apply(a: LoginRequest[AnyContent] => Result) : Action[AnyContent] = {
    apply(BodyParsers.parse.anyContent)(a)
  }

  def apply(a: => Result): Action[AnyContent] = apply(_ => a)

}

object LoginAction extends LoginActionBuilder
