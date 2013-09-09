package utils.js

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Codec, AnyContent, Request, Call}
import scala.Some
import play.api.http.{ContentTypes, ContentTypeOf, Writeable}
import play.api.data.Form
import play.api.i18n.Lang


sealed abstract class LoginResult {
}

case class LoginSuccess(obligation: Option[String], toUrl: Option[String]) extends LoginResult {
}

object LoginSuccess {
  def apply(obligation: Any) = new LoginSuccess(Some(obligation.toString), None)
  def apply(toUrl: Call)(implicit request: Request[AnyContent]) = new LoginSuccess(None, Some(toUrl.absoluteURL()))

  implicit val LoginSuccessWrites: Writes[LoginSuccess] = (
    (__ \ "obligation").writeNullable[String] and
      (__ \ "toUrl").writeNullable[String]
    )(unlift(LoginSuccess.unapply))

  implicit def LoginSuccessContentType(implicit codec: Codec): ContentTypeOf[LoginSuccess] = {
    ContentTypeOf(Some(ContentTypes.JSON))
  }
  implicit def LoginSuccessWriteables(implicit codec: Codec): Writeable[LoginSuccess] = {
    Writeable(e =>  Writeable.writeableOf_JsValue(codec).transform(Json.toJson(e)))
  }
}

case class LoginFailure(errors: JsValue) extends LoginResult {
}

object LoginFailure {
  def apply[T](errorForm: Form[T])(implicit lang : Lang) = new LoginFailure(errorForm.errorsAsJson)
  def apply(globalErrors: Seq[(String, String)]) = new LoginFailure(Json.toJson(Map("" -> globalErrors.map(_._2))))


  implicit val LoginFailureWrites: Writes[LoginFailure] = new Writes[LoginFailure] {
    def writes(o: LoginFailure): JsValue = {
      Json.obj("errors" -> o.errors)
    }
  }

  implicit def LoginFailureContentType(implicit codec: Codec): ContentTypeOf[LoginFailure] = {
    ContentTypeOf(Some(ContentTypes.JSON))
  }
  implicit def LoginFailureWriteables(implicit codec: Codec): Writeable[LoginFailure] = {
    Writeable(e =>  Writeable.writeableOf_JsValue(codec).transform(Json.toJson(e)))
  }
}


