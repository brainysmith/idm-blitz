package services.login

import play.api.mvc._
import services.login.LoginContext._
import LoginModule._
import com.blitz.idm.app._
import scala.util.Try
import controllers.routes
import conf.WlApp._
import play.api.mvc.Call
import scala.util.Success
import scala.util.Failure
import scala.Some

/**
  */
object loginManager {

  //todo: change it
  private val tmpCompleteRedirectCall = routes.Login.getPage()

  private val loginModulesMeta =
    (for ((clazz, params) <- conf.loginModules) yield new LoginModuleMeta(clazz, params)).toArray.sorted

  if (loginModulesMeta.isEmpty) {
    appLogWarn("authentication may not work: there aren't login modules found in the configuration")
  } else {
    appLogTrace("read login modules from the configuration: {}", loginModulesMeta)
  }

  private val loginModules = loginModulesMeta.map(_.newInstance)

  def apply(redirect: Call => Result)(loginFail: Seq[(String, Seq[Any])] => Result)
           (implicit lc: LoginContext, request: Request[AnyContent]) : Result = {
    appLogTrace("got login request [login context = {}, request = {}]", lc, request)

    lc match {
      case lcImpl: LoginContextImpl => {
        chain(lcImpl) match {
          case Success(PRE_AUTH_IS_REQUIRE) => {
            appLogDebug("pre-authentication is required")
            lcImpl.setStatus(DO_PRE_AUTH_STATUS)
            loginFail(lc.getErrors)
          }
          case Success(SUCCESS) => {
            appLogDebug("authentication is successful [login context = {}]", lc)
            lcImpl.clearLoginModule
            lcImpl.getObligation match {
              case Some(obligation) => {
                lcImpl.setStatus(DO_OBLIGATION_STATUS)
                appLogDebug("get an obligation, sent obligation redirect [obligation={}]", obligation)
                redirect(obligation)
              }
              case None => {
                lcImpl.setStatus(AUTH_SUCCESS_STATUS)
                appLogDebug("authentication process is complete successfully [login context = {}]", lc)

                //todo: thinking about why will be made the response to SP
                redirect(tmpCompleteRedirectCall)
              }
            }
          }
          case Success(FAIL) => {
            lcImpl.setStatus(AUTH_FAIL_STATUS)
            lc.getLoginModule.fold[Result]({
              appLogError("the authentication process is stopped: there aren't login modules to process the login " +
                "request [login context = {}]", lcImpl)
              throw new IllegalStateException("there aren't login modules to process the login request")
            }
            )(a => {
              appLogDebug("the authentication process is stopped: authentication is fail [login context = {}]", lc)
              loginFail(lc.getErrors)
            })
          }
          case Failure(e) => {
            appLogError("authentication can't be perform [runtime exception = {}]", e)
            throw e
          }
          case Success(unknownRes) => {
            appLogError("authentication can't be perform: unknown authentication result [result = {}], please, email to the technical support", unknownRes)
            throw new RuntimeException("unknown authentication result")
          }
        }
      }
      case _ => {
        appLogError("an unknown implementation of the login context: {}", lc)
        throw new RuntimeException("an unknown implementation of the login context, please, email to the technical support")
      }
    }
  }

  //todo: realized it
  /**
   * Retrieve the next point of the authentication process to redirect the current subject.
   * @return if the current state is not a SUCCESS then returns None otherwise a next point.
   */
  def getNextPoint(implicit lc: LoginContext, request: Request[AnyContent]): Option[Call] = {
    throw new UnsupportedOperationException("Hasn't realized yet.")
  }

  //chain to perform the main authentication process
  private def chain(lcImplIn: LoginContextImpl)(implicit request: Request[AnyContent]): Try[Int] = {
    implicit val lcImpl = lcImplIn
    for {
      dummy <- Try({
        if (lcImpl.getStatus == AUTH_SUCCESS_STATUS || lcImpl.getStatus == AUTH_FAIL_STATUS) {
          appLogError("the authentication process is stopped: the login process has already completed [login context = {}]", lcImpl)
          throw new IllegalStateException("the login process has already completed")
        }
      })
      curLoginModule <- Try({
        lcImpl.getLoginModule
      })
      result <- Try[Int]({
        curLoginModule.fold(loginModules.filter(_.isYours).foldLeft[Int](FAIL)((prevRes, a) => {
          prevRes match {
            case FAIL => {
              appLogTrace("selected a new login module [login module = {}]", a)
              lcImpl.setLoginModule(a)
              a.`do`
            }
            case _ => prevRes
          }
        }))(implicit a => {
          appLogTrace("got login module from the current login context [login module = {}]", a)
          a.`do`
        })
      })
    } yield result
  }
}
