package utils.security

import com.blitz.idm.app._
import play.api.mvc._
import services.login.LoginContextImpl
import com.blitz.idm.app.json.{JVal, Json}

/**
 */


trait LoginActionBuilder extends Results {

  val LC_KEY_NAME = "lc"

  def apply[A](bodyParser: BodyParser[A])(a: LoginRequest[A] => Result) = {
    Action(bodyParser) { implicit request =>
      implicit val lc: LoginContextImpl = request.session.get(LC_KEY_NAME).map(lcStr => {
        appLogTrace("got the following login context from the current HTTP session: {}", lcStr)
        Json.fromJson[LoginContextImpl](JVal.parseStr(lcStr))
        /*LoginContextImpl.fromJson(lcStr)*/
      }).getOrElse({
        appLogTrace("can't find the login context in the current HTTP session: make a new one")
        new LoginContextImpl()
      })

      a(LoginRequest(request, lc)).withSession(LC_KEY_NAME -> Json.toJson(lc).toJson)
    }
  }

  def apply(a: LoginRequest[AnyContent] => Result) : Action[AnyContent] = {
    apply(BodyParsers.parse.anyContent)(a)
  }

  def apply(a: => Result): Action[AnyContent] = apply(_ => a)

}

object LoginAction extends LoginActionBuilder
