package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
<<<<<<< HEAD

=======
>>>>>>> 77c4aa530a1f5605f1d3ed14a7051c350d5ea6c7


object Login extends Controller {

  val loginForm = Form(
    tuple(
      "lgn" -> nonEmptyText,
      "pswd"  -> nonEmptyText
    )
  )

  def getPage = Action {implicit request =>{
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
