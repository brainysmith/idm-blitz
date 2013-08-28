package services.login

import play.api.mvc._
import services.login.LoginContext._
import Authenticator._
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

  private val authenticatorsMeta =
    (for ((clazz, params) <- conf.authenticators) yield new AuthenticatorMeta(clazz, params)).toArray.sorted

  if (authenticatorsMeta.isEmpty) {
    appLogWarn("authentication may not work: there aren't authenticators found in the configuration\n")
  } else {
    appLogTrace("read authenticators from the configuration: {}\n", authenticatorsMeta)
  }

  private val authenticators = authenticatorsMeta.map(_.newInstance)

  def apply(redirect: Call => Result)(loginFail: Array[String] => Result)
           (implicit lc: LoginContext, request: Request[AnyContent]) : Result = {
    appLogTrace("got login request [login context = {}, request = {}]\n", lc, request)

    lc match {
      case lcImpl: LoginContextImpl => {
        chain(lcImpl) match {
          case Success(PRE_AUTH_IS_REQUIRE) => {
            appLogDebug("pre-authentication is required\n")
            lcImpl.setStatus(DO_PRE_AUTH_STATUS)
            loginFail(lc.getMessages)
          }
          case Success(SUCCESS) => {
            appLogDebug("authentication is successful [login context = {}]\n", lc)
            lcImpl.clearAuthenticator
            lcImpl.getObligation match {
              case Some(obligation) => {
                lcImpl.setStatus(DO_OBLIGATION_STATUS)
                appLogDebug("get an obligation, sent obligation redirect [obligation={}]\n", obligation)
                redirect(obligation)
              }
              case None => {
                lcImpl.setStatus(AUTH_SUCCESS_STATUS)
                appLogDebug("authentication process is complete successfully [login context = {}]\n", lc)

                //todo: thinking about why will be made the response to SP
                redirect(tmpCompleteRedirectCall)
              }
            }
          }
          case Success(FAIL) => {
            lcImpl.setStatus(AUTH_FAIL_STATUS)
            lc.getAuthenticator.fold[Result]({
              appLogError("the authentication process is stopped: there aren't authenticators to process the login " +
                "request [login context = {}]\n", lcImpl)
              throw new IllegalStateException("there aren't authenticators to process the login request")
            }
            )(a => {
              appLogDebug("the authentication process is stopped: authentication is fail [login context = {}]\n", lc)
              loginFail(lc.getMessages)
            })
          }
          case Failure(e) => {
            appLogError("authentication can't be perform [runtime exception = {}]\n", e)
            throw e
          }
          case Success(unknownRes) => {
            appLogError("authentication can't be perform: unknown authentication result [result = {}], please, email to the technical support\n", unknownRes)
            throw new RuntimeException("unknown authentication result")
          }
        }
      }
      case _ => {
        appLogError("an unknown implementation of the login context: {}\n", lc)
        throw new RuntimeException("an unknown implementation of the login context, please, email to the technical support")
      }
    }
  }

  //chain to perform the main authentication process
  private def chain(lcImplIn: LoginContextImpl)(implicit request: Request[AnyContent]): Try[Int] = {
    implicit val lcImpl = lcImplIn
    for {
      dummy <- Try({
        if (lcImpl.getStatus == AUTH_SUCCESS_STATUS || lcImpl.getStatus == AUTH_FAIL_STATUS) {
          appLogError("the authentication process is stopped: the login process has already completed [login context = {}]\n", lcImpl)
          throw new IllegalStateException("the login process has already completed")
        }
      })
      curAuthenticator <- Try({
        lcImpl.getAuthenticator
      })
      result <- Try[Int]({
        curAuthenticator.fold(authenticators.filter(_.isYours).foldLeft[Int](FAIL)((prevRes, a) => {
          prevRes match {
            case FAIL => {
              appLogTrace("selected a new authenticator [authenticator = {}]\n", a)
              lcImpl.setAuthenticator(a)
              a.`do`
            }
            case _ => prevRes
          }
        }))(implicit a => {
          appLogTrace("got authenticator from the current login context [authenticator = {}]\n", a)
          a.`do`
        })
      })
    } yield result
  }
}
