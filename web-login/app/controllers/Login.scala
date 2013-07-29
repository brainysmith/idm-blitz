package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import com.blitz.idm.app._
import org.slf4j.LoggerFactory

object Login extends Controller {

  val loginForm = Form(
    tuple(
      "lgn" -> nonEmptyText,
      "pswd"  -> nonEmptyText
    )
  )

  def getPage = Action {implicit request =>{
    appLogDebug("Logging...\n")
    Ok(views.html.login(loginForm))
  }
  }

  def authenticate = Action {
    implicit request => {
      loginForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(views.html.login(errorForm))
        },
        form => {
          Ok("Good! Lgn: " + form._1 + ", pswd: " + form._2 + ".")
        }
      )
    }
  }

}
