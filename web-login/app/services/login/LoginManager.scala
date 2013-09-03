package services.login


import com.blitz.idm.app._
import scala.util.{Try, Success, Failure}
import controllers.routes
import conf.WlApp._
import play.api.mvc.{AnyContent, Request, Call}
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
   * Perform a preparing to authentication:
   *  - move the current login context into processing state with specified method;
   *  - pools all available login modules and adds into the current login context those which are ready to perform
   *    authentication again the current login context. Login modules can add its necessary for the subsequent
   *    authentication parameters into the login context.
   *
   * @param method the method of the authentication.
   * @param lc the login context. If it is absent then new will be created.
   * @param request the current request.
   */
  def start(method: Int)(implicit lc: LoginContext = LoginContext(), request: Request[AnyContent]) = {
    appLogTrace("start a new authentication process [method = {}, lc = {}]", method, lc)

    lc match {
      case lcImpl: LoginContextImpl => {
        Try({
          //check that the current state of the login process is appropriate for the current operation
          if (lcImpl.getStatus == LoginStatus.SUCCESS) {
            appLogError("the authentication process is stopped: the login process has already completed " +
              "[login context = {}]", lcImpl)
            throw new IllegalStateException("the login process has already completed")
          }

          lcImpl.setCurrentMethod(method)
          lcImpl.clearLoginModulesToProcess()
          //find the login modules to process
          loginModules.filter(lm => lm.start).foreach(lm => {
            appLogTrace("a new login module has been added to process [method = {}, login module = {}]",
              method, lm)
            lcImpl.addLoginModuleToProcess(lm)
          })

          if (lcImpl.getLoginModulesToProcess().isEmpty) {
            appLogError("there isn't login module which is able to perform the authentication with specified method " +
              "[method = {}, lc = {}]", method, lc)
            throw new IllegalStateException("there isn't login module which is able to perform the authentication " +
              "with specified method")
          }
        }) match {
          case Success(_) => {
            lcImpl.setStatus(LoginStatus.PROCESSING)
            appLogDebug("a new authentication process for the specified method has been started [method={}, login context = {}]", method, lcImpl)
          }
          case Failure(e) => {
            lcImpl.setStatus(LoginStatus.FAIL)
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
   * Perform the authentication again the login modules which was selected on start step with "FIRST SUCCESS" strategy.
   * todo: add description
   * @param success
   * @param fail
   * @param lc
   * @param request
   * @tparam A
   * @return
   */
  def `do`[A](success: Option[Call] => A)(fail: Seq[(String, String)] => A)
          (implicit lc: LoginContext, request: Request[AnyContent]) = {
    appLogTrace("perform the authentication [login context = {}]", lc)

    lc match {
      case lcImpl: LoginContextImpl => {
        //check the current status
        if (lcImpl.getStatus != LoginStatus.PROCESSING) {
          appLogError("authentication can't be performed: a wrong status. Ensure that your perform start on login " +
            "manager before [lc = {}]", lc)
          throw new IllegalStateException("authentication can't be performed: a wrong status. Ensure that your perform " +
            "start on login manager before")
        }

        //perform the authentication with "FIRST SUCCESS" strategy
        Try({
          lcImpl.getLoginModulesToProcess().foldLeft[Result](Result.FAIL)((prevRes, lm) => {
            if (prevRes == Result.FAIL) {
              appLogTrace("try to authenticate by a new login module [lm = {}]", lm)
              val iRes = lm.`do`
              if (lm.`do` == Result.SUCCESS) {
                appLogTrace("authentication by login module is successfully [lm = {}]", lm)
                lcImpl.setLoginModule(lm)
              } else {
                appLogTrace("fail authentication by login module [lm = {}]", lm)
              }
              iRes
            } else {
              prevRes
            }
          })
        }) match {
          case Success(result) => {
            result match {
              case Result.SUCCESS => {
                appLogDebug("authentication is successful [lc = {}]", lcImpl)
                //todo: add completed authentication method to the login context
                loginFlow.getNextPoint.fold[A]({
                  lcImpl.setStatus(LoginStatus.SUCCESS)
                  appLogDebug("the login process is completed successfully [lc = {}]", lcImpl)
                  success(Some(tmpCompleteRedirectCall))
                })(nextPoint => {
                  appLogDebug("go to the next point [lc = {}]", lcImpl)
                  success(Some(nextPoint))
                })
              }
              case Result.FAIL => {
                lcImpl.setStatus(LoginStatus.FAIL)
                appLogDebug("authentication is failed [lc = {}]", lcImpl)
                fail(lcImpl.getErrors)
              }
              case _ @ unknownRes => {
                appLogError("authentication can't be perform: unknown result code of the authentication [code = {}, " +
                  "lc = {}]", unknownRes, lcImpl)
                throw new RuntimeException("authentication can't be perform: unknown result code of the authentication")
              }
            }
          }
          case Failure(e) => {
            lcImpl.setStatus(LoginStatus.FAIL)
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

  /**
   * Prepares a preparing to authentication (start) and subsequent authentication (do) in one call.
   *
   * @param method the required method of the authentication.
   * @param success the function which will be called in case the login is successful.
   * @param fail the function which will be called in case the login is failed.
   * @param lc the current login context.
   * @param request the http request.
   */
  def apply[A](method: Int)(success: Option[Call] => A)(fail: Seq[(String, String)] => A)
           (implicit lc: LoginContext = LoginContext(), request: Request[AnyContent]) = {
    start(method)
    `do`(success)(fail)
  }
}
