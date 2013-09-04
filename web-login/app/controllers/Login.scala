package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import services.login.{LoginContext, BuildInMethods, LoginManager}
import com.blitz.idm.app._

object Login extends Controller {

  val basicForm = Form(
    tuple(
      "lgn" -> nonEmptyText,
      "pswd"  -> nonEmptyText
    )
  )

  def getPage = Action {implicit request =>{
      Ok(views.html.login(basicForm))
    }
    appLogDebug("Logging...")
    Ok(views.html.login(basicForm))
  }

  /*todo: change implementation*/
  def basicLogin = Action {
    implicit request => {
      basicForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(views.html.login(errorForm))
        },
        form => {
          implicit val lc = LoginContext.basic(form)
          LoginManager[Result](BuildInMethods.BASIC)(call => {
            //authentication is successful
            Ok("Good! Lgn: " + form._1 + ", pswd: " + form._2 + ".")
          })(errors => {
            BadRequest(views.html.login(errors.foldLeft(basicForm.fill((form._1, "")))((frm, msg) => frm.withGlobalError(msg._2))))
          })
        }
      )
    }
  }

  def smartCardLogin = Action {
    implicit request => NotImplemented
  }

  def otpLogin = Action {
    implicit request => NotImplemented
  }
}
