package services.login

import play.api.mvc._
import com.blitz.idm.app._
import scala.util.Try
import controllers.routes
import conf.WlApp._
import play.api.mvc.Call
import scala.util.Success
import scala.util.Failure
import scala.annotation.implicitNotFound

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
        /*if current method of the login context is None or it is not equal to the processing method then clear
          the selected authenticator and set the new method in the current login context*/
        if (lc.getCurrentMethod.fold(true)(_ != method)) {
          appLogTrace("setting the new authentication method and clear the current login module")
          lcImpl.setCurrentMethod(method)
          lcImpl.clearLoginModule
        }

        chain(lcImpl) match {
          case Success(isAuth) => {
            if (isAuth) {
              appLogDebug("authentication is successful [lc = {}]", lc)
              loginFlow.getNextPoint.fold({
                lcImpl.setStatus(LoginStatuses.SUCCESS)
                appLogDebug("the login process is completed successfully [lc = {}]", lc)
                success(tmpCompleteRedirectCall)
              })(nextPoint => {
                appLogDebug("go to the next point [lc = {}]", lc)
                success(nextPoint)
              })
            } else {
              lcImpl.setStatus(LoginStatuses.FAIL)
              appLogDebug("authentication is failed [lc = {}]", lc)
              fail(lc.getErrors)
            }
          }
          case Failure(e) => {
            lcImpl.setStatus(LoginStatuses.FAIL)
            appLogError("authentication can't be perform [runtime exception = {}]", e)
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

  //chain to perform the main authentication process
  private def chain(lcImplIn: LoginContextImpl)(implicit request: Request[AnyContent]): Try[Boolean] = {
    implicit val lcImpl = lcImplIn
    for {
      dummy <- Try({
        if (lcImpl.getStatus == LoginStatuses.SUCCESS.id) {
          appLogError("the authentication process is stopped: the login process has already completed [login context = {}]", lcImpl)
          throw new IllegalStateException("the login process has already completed")
        } else {
          lcImpl.setStatus(LoginStatuses.PROCESSING)
        }
      })
      curLoginModule <- Try({
        lcImpl.getLoginModule
      })
      result <- Try[Boolean]({
        curLoginModule.fold(loginModules.filter(_.isYours).foldLeft[Boolean](false)((prevRes, a) => {
          if (prevRes) {
            prevRes
          } else {
            appLogTrace("selected a new login module [login module = {}]", a)
            lcImpl.setLoginModule(a)
            a.`do`
          }
        }))(implicit a => {
          appLogTrace("got login module from the current login context [login module = {}]", a)
          a.`do`
        })
      })
    } yield result
  }
}
