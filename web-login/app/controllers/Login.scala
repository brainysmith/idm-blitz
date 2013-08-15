package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import services.login.LoginContext._
import services.login.loginManager
import com.blitz.idm.app._
import play.api.i18n.Messages

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
    appLogDebug("Logging...\n")
    Ok(views.html.login(basicForm))
  }

  def basicLogin = Action {
    implicit request => {
      basicForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(views.html.login(errorForm))
        },
        form => {
          Option(basic(form))
            .fold[Result](BadRequest(""))(implicit lc => {
                loginManager(call => {
                  Ok("Good! Lgn: " + form._1 + ", pswd: " + form._2 + ".")
                })(errors => {
                  BadRequest(views.html.login(errors.foldLeft(basicForm.fill((form._1, "")))((frm, msg) => frm.withGlobalError(Messages(msg)))))
                })
              }
            )
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
