package services.login

import play.api.mvc._
import com.blitz.idm.app._
import scala.util.{Try, Success, Failure}
import controllers.routes
import conf.WlApp._
import play.api.mvc.Call
import scala.annotation.implicitNotFound
import LoginModule._

/**
  */
@implicitNotFound("No implicit login context of request found.")
object LoginManager {

  //todo: change it
  private val tmpCompleteRedirectCall = routes.Login.getPage()

  private val loginModulesMeta =
    (for ((clazz, params) <- conf.loginModules) yield new LoginModuleMeta(clazz, params)).toArray.sorted

  if (loginModulesMeta.isEmpty) {
    appLogWarn("authentication may not work: there aren't login modules found in the configuration")
  } else {
    appLogDebug("read login modules from the configuration: {}", loginModulesMeta)
  }

  private val loginModules = loginModulesMeta.map(_.newInstance)

  private val loginFlow = conf.loginFlow.fold[LoginFlow]({
    appLogDebug("use the default login flow")
    DefaultLoginFlow
  })(className => {
    appLogDebug("getting the custom login flow [class = {}]", className)
    this.getClass.getClassLoader.loadClass(className + "$").asInstanceOf[LoginFlow]
  })

  /**
   * Perform the first phase of the two-phase authentication.
   *
   * @param method
   * @param lc
   * @param request
   */
  def start(method: Int)(implicit lc: LoginContext = LoginContext(), request: Request[AnyContent]) = {
    appLogTrace("start a new authentication process [method = {}]", method)

    lc match {
      case lcImpl: LoginContextImpl => {
        Try({
          prepareForStart(method)

          //find the first appropriate login module
          loginModules.find(lm => lm.start).fold[LoginContext]({
            //if there isn't an appropriate login module
            appLogError("there isn't login module which is able to perform the authentication with specified method [method = {}]", method)
            throw new IllegalArgumentException("there isn't login module which is able to perform the authentication with specified method")
          })(lm => {
            appLogTrace("a new login module has been selected [method = {}, login module = {}]", method, lm)
            lcImpl.setLoginModule(lm)
          })

        }) match {
          case Success(_) => {
            appLogDebug("a new authentication process for the specified method has been started [method={}, login context = {}]",
              method, _)
          }
          case Failure(e) => {
            lcImpl.setStatus(LoginStatuses.FAIL)
            appLogError("a new authentication can't be started [method = {}, runtime exception = {}]", method, e)
            throw e
          }

        }
      }
      case _ => {
        appLogError("an unknown implementation of the login context: {}", lc)
        throw new RuntimeException("an unknown implementation of the login context, please, email to the technical support")
      }
    }
  }


  /**
   * Perform the second phase of the two-phase authentication.
   *
   * @param success
   * @param fail
   * @param lc
   * @param request
   * @tparam A
   * @return
   */
  def `do`[A](success: Option[Call] => A)(fail: Seq[(String, String)] => A)
          (implicit lc: LoginContext, request: Request[AnyContent]): A = {
    appLogTrace("perform authentication [login context = {}]", lc)

    lc match {
      case lcImpl: LoginContextImpl => {
        lcImpl.getLoginModule.fold[A]({
          //if empty
          appLogError("couldn't find a login module in the current login context. Ensure that your perform start " +
            "on login manager before [lc = {}]", lc)
          throw new IllegalStateException("couldn't find a login module in the current login context. Ensure that your " +
            "perform start on login manager before [lc = {}]")
        })(lm => complete(Try({lm.`do`})))
      }
      case _ => {
        appLogError("an unknown implementation of the login context: {}", lc)
        throw new RuntimeException("an unknown implementation of the login context, please, email to the technical support")
      }
    }
  }

  /**
   * Perform the single-phase authentication.
   *
   * @param method the required method of the authentication.
   * @param success the function which will be called in case the login is successful.
   * @param fail the function which will be called in case the login is failed.
   * @param lc the current login context.
   * @param request the http request.
   */
  def apply(method: Int)(success: Call => Result)(fail: Seq[(String, String)] => Result)
           (implicit lc: LoginContext, request: Request[AnyContent]) = {
    appLogTrace("got login request [login context = {}, request = {}]", lc, request)

    lc match {
      case lcImpl: LoginContextImpl => {
        prepareForStart(method)
        complete(Try({
          //traverse for all appropriate login modules before the first success
          loginModules.filter(_.start).foldLeft[Int](Result.FAIL.id)((prevRes, lm) => {
            if (prevRes == Result.FAIL.id) {
              appLogTrace("selected a new login module [login module = {}]", lm)
              lcImpl.setLoginModule(lm)
              lm.`do`
            } else {
              prevRes
            }
          })
        }))
      }
      case _ => {
        appLogError("an unknown implementation of the login context: {}", lc)
        throw new RuntimeException("an unknown implementation of the login context, please, email to the technical support")
      }
    }
  }


  private def prepareForStart(method: Int)(implicit lcImpl: LoginContextImpl) = {
    //check the current status
    if (lcImpl.getStatus == LoginStatuses.SUCCESS.id) {
      appLogError("the authentication process is stopped: the login process has already completed [login context = {}]", lcImpl)
      throw new IllegalStateException("the login process has already completed")
    } else {
      lcImpl.setStatus(LoginStatuses.PROCESSING)
    }

    /*if current method of the login context is None or it is not equal to the processing method then clear
      the selected authenticator and set the new method in the current login context*/
    if (lcImpl.getCurrentMethod.fold(true)(_ != method)) {
      appLogTrace("setting the new authentication method and clear the current login module")
      lcImpl.setCurrentMethod(method)
      lcImpl.clearLoginModule
    }
  }

  private def complete[A](tResult: Try[Int])
                         (implicit success: Option[Call] => A, fail: Seq[(String, String)] => A,
                          lcImpl: LoginContextImpl, request: Request[AnyContent]): A = {
    tResult match {
      case Success(result) => {
        result match {
          case Result.SUCCESS.id => {
            appLogDebug("authentication is successful [lc = {}]", lcImpl)
            loginFlow.getNextPoint.fold[A]({
              lcImpl.setStatus(LoginStatuses.SUCCESS)
              appLogDebug("the login process is completed successfully [lc = {}]", lcImpl)
              //todo: add completed authentication method to the login context
              success(Some(tmpCompleteRedirectCall))
            })(nextPoint => {
              appLogDebug("go to the next point [lc = {}]", lcImpl)
              //todo: add completed authentication method to the login context
              success(Some(nextPoint))
            })
          }
          case Result.PARTIALLY_COMPLETED.id => {
            appLogDebug("authentication has completed partially [lc = {}]", lcImpl)
            success(None)
          }
          case Result.FAIL.id => {
            lcImpl.setStatus(LoginStatuses.FAIL)
            appLogDebug("authentication is failed [lc = {}]", lcImpl)
            fail(lcImpl.getErrors)
          }
          case _ => {
            appLogError("authentication can't be perform: unknown result code of the authentication [code = {}, lc = {}]",
              _, lcImpl)
            throw new RuntimeException("authentication can't be perform: unknown result code of the authentication")
          }
        }
      }
      case Failure(e) => {
        lcImpl.setStatus(LoginStatuses.FAIL)
        appLogError("authentication can't be perform [runtime exception = {}]", e)
        throw e
      }
    }
  }
}
