package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import services.login.LoginContext._
import services.login.LoginContext


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
  }

  def basicLogin = Action {
    implicit request => {
      basicForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(views.html.login(errorForm))
        },
        form => {
          Option(LoginContext(basic(form)))
            .fold(BadRequest(""))(implicit lc => {
                Ok("Good! Lgn: " + form._1 + ", pswd: " + form._2 + ".")
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
