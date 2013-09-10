package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.{FormError, Form}
import services.login.{BasicLoginModule, LoginContext, BuildInMethods, LoginManager}
import utils.security.{LoginRequest, LoginAction}
import com.blitz.idm.app.json.Json
import com.blitz.idm.app._
import utils.js.{LoginResult, LoginFailure, LoginSuccess}

object Login extends Controller {

  val basicForm = Form(
    tuple(
      "login" -> nonEmptyText,
      "pswd"  -> nonEmptyText
    )
  )

  val pswdChangeForm = Form(
    tuple(
      "curPswd" -> nonEmptyText,
      "newPswd"  -> nonEmptyText
    )
  )

  def getPage = Action { implicit request => {
      Ok(views.html.login(basicForm))
    }
  }


  /*todo: change implementation (move to ajax)*/
  def basicLogin = LoginAction {
    implicit loginRequest => {
      basicForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(LoginFailure(errorForm))
        },
        form => {
          _basicLogin(form._2, Some(form._1))
        }
      )
    }
  }

  /*todo: change implementation*/
  def altPswd = LoginAction {
    implicit loginRequest => {
      pswdChangeForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(LoginFailure(errorForm))
        },
        form => {
          implicit val lc: LoginContext = loginRequest.getLoginContext
          val basicLm = lc.getLoginModule[BasicLoginModule].getOrElse({
            appLogError("can't perform the operation: a login module not found")
            throw new RuntimeException("can't perform the operation: a login module not found")
          })

          if (basicLm.changePassword(form._1, form._2)) {
            appLogTrace("password has been changed successfully")
            _basicLogin(form._2)
          } else {
            appLogTrace("password changing has failed [errors={}]", lc.getErrors)
            BadRequest(LoginFailure(lc.getErrors))
          }
        }
      )
    }
  }

  private def _basicLogin(pswd: String, loginOpt: Option[String] = None)(implicit lr: LoginRequest[AnyContent]): Result = {
    implicit val lc: LoginContext = loginOpt.fold({
      lr.getLoginContext withCredentials Json.obj("pswd" -> pswd)
    })(login => {
      lr.getLoginContext withCredentials Json.obj("login" -> login, "pswd" -> pswd)
    })

    LoginManager[Result](BuildInMethods.BASIC)(callOpt => {
      callOpt.fold[Result]({
        //partial success
        lc.getObligation.fold[Result]({
          //todo: change it
          throw new RuntimeException("it's impossible")
        })(obligation => {
          obligation match {
            case BasicLoginModule.Obligation.CHANGE_PASSWORD => {
              Ok(LoginSuccess(obligation))
            }
            case _ => {
              //todo: change it
              throw new RuntimeException("unknown obligation")
            }
          }
        })
      })(call => {
        Ok(LoginSuccess(call))
      })
    })(errors => {
      BadRequest(LoginFailure(errors))
    })
  }

  def smartCardLogin = Action {
    implicit request => NotImplemented
  }

  def otpLogin = Action {
    implicit request => NotImplemented
  }
}
