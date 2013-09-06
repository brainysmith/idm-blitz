package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data.Form
import services.login.{LoginContext, BuildInMethods, LoginManager}
import play.api.libs.json.Json
import utils.security.LoginAction
import com.blitz.idm.app.json.{JStr, JObj}

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
          session.get("test").map(time => System.out.printf("time: %s\n", time)).getOrElse(System.out.println("empty"))
          BadRequest(Json.obj("errors" -> errorForm.errorsAsJson)).withSession("test" -> System.currentTimeMillis().toString)
        },
        form => {
          //implicit val lc = LoginContext.basic(form)
          implicit val lc = loginRequest.getLoginContext withCredentials JObj(Seq("lgn" -> JStr(form._1), "pswd" -> JStr(form._2)))


          LoginManager[Result](BuildInMethods.BASIC)(callOpt => {
            callOpt.fold[Result]({
              //partial success
              lc.getObligation.fold[Result]({
                //todo: change it
                throw new RuntimeException("it's impossible")
              })(obligation => {
                obligation match {
                  case "change_password" => {
                    //todo: add login context
                    Ok(Json.obj("result" -> Json.obj("obligation" -> "change_password")))
                  }
                  case _ => {
                    //todo: change it
                    throw new RuntimeException("unknown obligation")
                  }
                }
              })
            })(call => {
              //todo: add login context
              Ok(Json.obj("result" -> Json.obj("toUrl" -> call.absoluteURL())))
            })
          })(errors => {
            BadRequest(Json.obj("errors" -> errors.foldLeft(basicForm)((frm, msg) => frm.withGlobalError(msg._2)).errorsAsJson))
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
/*  def changePswd = Action {
    implicit request => NotImplemented
  }*/
  def altPswd = Action {
    implicit request => {
      pswdChangeForm.bindFromRequest.fold(
        errorForm => {
          BadRequest(Json.obj("errors" -> errorForm.errorsAsJson))
        },
        form => {
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
