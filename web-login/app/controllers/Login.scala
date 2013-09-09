package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.{FormError, Form}
import services.login.{BasicLoginModule, LoginContext, BuildInMethods, LoginManager}
import play.api.libs.json.{JsObject, Json}
import utils.security.LoginAction
import com.blitz.idm.app.json.{JStr, JObj}
import com.blitz.idm.app._
import utils.js.{LoginFailure, LoginSuccess}

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
          //implicit val lc = LoginContext.basic(form)
          implicit val lc: LoginContext = loginRequest.getLoginContext withCredentials JObj(Seq("login" -> JStr(form._1), "pswd" -> JStr(form._2)))

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
      )
    }
  }

  /*todo: change implementation*/
  def altPswd = LoginAction {
    implicit loginRequest => {
      pswdChangeForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(Json.obj("errors" -> errorForm.errorsAsJson))
        },
        form => {
          implicit val lc: LoginContext = loginRequest.getLoginContext

          val basicLm = lc.getLoginModule[BasicLoginModule].getOrElse({
            appLogError("can't perform the operation: a login module not found")
            throw new RuntimeException("can't perform the operation: a login module not found")
          })

          basicLm.changePassword(form._1, form._2)
          FormError





          //implicit val lc: LoginContext = loginRequest.getLoginContext withCredentials JObj(Seq("lgn" -> JStr(form._1), "pswd" -> JStr(form._2)))
          LoginManager.`do`(callOpt => {
            callOpt.fold[Result]({
              appLogError("stop the authentication process: unexpected behavior, a next point not found.")
              throw new RuntimeException("stop the authentication process: unexpected behavior, a next point not found.")
            })(call => {
              Ok(Json.obj("result" -> Json.obj("toUrl" -> call.absoluteURL())))
            })
          })(errors => {
            BadRequest(Json.obj("errors" -> errors.foldLeft(pswdChangeForm)((frm, msg) => frm.withGlobalError(msg._2)).errorsAsJson))
          })
          throw new UnsupportedOperationException("Hasn't realized yet.")
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
