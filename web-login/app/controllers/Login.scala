package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import services.login.{BasicLoginModule, LoginContext, BuildInMethods, LoginManager}
import com.blitz.idm.app._

object Login extends Controller {

  val basicForm = Form(
    tuple(
      "lgn" -> nonEmptyText,
      "pswd"  -> nonEmptyText
    )
  )

  val pswdChangeForm = Form(
    tuple(
      "pswdCur" -> nonEmptyText,
      "pswdNew"  -> nonEmptyText
    )
  )

  def getPage = Action { implicit request => {
      Ok(views.html.login(basicForm))
    }
  }


  /*todo: change implementation (move to ajax)*/
  def basicLogin = Action {
    implicit request => {
      basicForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(views.html.login(errorForm))
        },
        form => {
          implicit val lc = LoginContext.basic(form)
          LoginManager[Result](BuildInMethods.BASIC)(callOpt => {
            callOpt.fold[Result]({
              //partial success
              lc.getObligation.fold[Result]({
                //todo: change it
                throw new RuntimeException("it's impossible")
              })(obligation => {
                obligation match {
                  case "change_password" => {
                    Ok(views.html.pswdChange(pswdChangeForm))
                  }
                  case _ => {
                    //todo: change it
                    throw new RuntimeException("unknown obligation")
                  }
                }
              })
            })(call => {
              //success
              Ok("Good! Lgn: " + form._1 + ", pswd: " + form._2 + ".")
            })
          })(errors => {
            BadRequest(views.html.login(errors.foldLeft(basicForm.fill((form._1, "")))((frm, msg) => frm.withGlobalError(msg._2))))
          })
        }
      )
    }
  }

/*  def getChangePswdPage = Action { implicit request => {
      Ok(views.html.pswdChange(pswdChangeForm))
    }
  }  */

  /*todo: change implementation (move to ajax)*/
  def changePswd = Action {
    implicit request => NotImplemented
  }
/*  def changePswd = Action {
    implicit request => {
      pswdChangeForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(views.html.pswdChange(errorForm))
        },
        form => {
          implicit val lc = LoginContext.basic(form)

        }
      )
    }
  }*/

  def smartCardLogin = Action {
    implicit request => NotImplemented
  }

  def otpLogin = Action {
    implicit request => NotImplemented
  }
}
