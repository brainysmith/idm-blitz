package services.login

import play.api.mvc.{AnyContent, Request}
import com.blitz.idm.app.CustomEnumeration

/**
 */

/*todo: actualize the description*/
trait LoginModule {

  /**
   * The method is called by the application to indicate to a login module that it is being place into service.
   * The application calls this method exactly once after instantiating the login module.The method must be completed
   * successfully before the login module is asked to do any work.
   * @param options - map of the options which was specified into the configuration.
   * @return - current instance of the login module.
   */
  def init(options: Map[String, String]): LoginModule


  /**
   * The method is called each time before a new authentication method is started. If the login module accept
   * the current login request against the input login context to perform authentication it must do some preparatory
   * actions (e.g. generate challenge) and return true otherwise false.
   * @param lc - the context of the current authentication process.
   * @param request - the login HTTP request.
   * @return true if the login module accept the current login request and false otherwise.
   */
  def start(implicit lc: LoginContext, request: Request[AnyContent]): Boolean

  /**
   * The method is called each time need to perform the authentication. The result controls subsequent
   * authentication process. The following represents a description their respective semantics:
   *    - SUCCESS - if the subject successfully authenticated. All claims will be included in the assertions.
   *    If there is obligation in the login context the controller must performs it successfully before
   *    continue the process. If there are warning in the login context the controller must shows it before
   *    continue the process.
   *    - FAIL - if the subject's authentication is failed. The authentication process is interrupted.
   *    A cause can be added to the list of the login context's errors.
   * @param lc - context of the current authentication process.
   * @param request - the login HTTP request.
   * @return result: success, fail.
   */
  def `do`(implicit lc: LoginContext, request: Request[AnyContent]): Result
}

sealed abstract class Result(private val _name: String) extends Result.Val {
  def name = _name
}

object Result extends CustomEnumeration[Result] {
  case object SUCCESS extends Result("success")
  case object FAIL extends Result("fail")
}


object LoginModule {
  def apply(className: String, params: Map[String, String]): LoginModule = {
    new LoginModuleMeta(className, params).newInstance
  }
}

