package services.login

import javax.security.auth.Subject
import play.api.mvc.{AnyContent, Request}
import scala.collection.mutable

/**
 * Context of the authentication process.
  */
trait LoginContext {

  /**
   * Return the current HTTP request.
   * @return request.
   */
  def getRequest: Request[AnyContent]


  /**
   * Returns a subject associated with the current authentication process.
   */
  def getSubject: Subject

  /**
   * Returns of the current status of the authentication process.
   * See the login's status constants of the current interface.
   * @return
   */
  def getStatus: Int

  /**
   * Returns the current login method of the authentication process.
   * See the login's method constants of the current interface.
   @return code of the login method.
   */
  def getMethod: Int

  /**
   * Returns login methods that have been successfully completed.
   * See the login's method constants of the current interface.
   * @return the bitmask of the login methods.
   */
  def getCompletedMethods: Int

  /**
   * Returns the authentication which was selected in the current login context.
   * @return None if an authenticator has not yet been selected and Some[Authenticator] otherwise.
   */
  def getAuthenticator: Option[Authenticator]

  /**
   * Returns the parameter's map which associated with the current authentication process.
   * If there is no parameters returns the empty map.
   * Each parameter has a name and a value.
   * @return map with parameters.
   */
  def getParams: mutable.Map[String, String]

  /**
   * Returns the obligation which must be executed before the authentication process will be completed.
   * @return string representing the obligation.
   */
  def getObligation: Option[String]

  /**
   * List of the message's keys which will be sent to the client. After message has been shown it is deleted from
   * the list.
   * @return list of the message's key.
   * */
  def getMessages: mutable.ArrayBuffer[String]
}

object LoginContext {
  //Login`s method constants
  val BASIC_METHOD = 1
  val SMART_CARD_METHOD = 2
  val OTP_METHOD = 4

  //Login`s status constants
  val INIT_STATUS = -1
  val SUCCESS_STATUS = 0
  val FAIL_STATUS = 1
  val ERROR_STATUS = 2
  val PRE_AUTH_STATUS = 3
  val DO_OBLIGATION_STATUS = 4

  //Obligation`s constants
  val OTP_OBLIGATION = "otp_required"

  def basic(lgnAndPwd: (String, String))(implicit request : Request[AnyContent]): LoginContextBuilder = {
    new LoginContextBuilder(BASIC_METHOD) + Credentials(lgnAndPwd)
  }

  def apply(lcb: LoginContextBuilder)(implicit request : Request[AnyContent]): LoginContext = {
    lcb.build
  }
}

class LoginContextBuilder(private val method: Int)(implicit request : Request[AnyContent]) {
  def +(param: (String, String)): LoginContextBuilder = {
    //todo: add a parameter to the login context
    this
  }

  def +(credentials: Credentials): LoginContextBuilder = {
    //todo: add credentials to the login context
    this
  }

  def build: LoginContext = {
    //todo: do it
    new LoginContextImpl(method, new Subject())
  }
}

private class LoginContextImpl(private val method: Int, private val subject: Subject)
                      (implicit val request: Request[AnyContent]) extends LoginContext {
  import LoginContext._

  private val params = new mutable.HashMap[String, String]()
  private val messages = new mutable.ArrayBuffer[String]()

  private var status = INIT_STATUS
  private var completedMethods = 0
  private var authenticator = None
  private var obligation = None

  def getRequest: Request[AnyContent] = request

  def getSubject: Subject = subject

  def getStatus: Int = status

  def getMethod: Int = method

  def getCompletedMethods: Int = completedMethods

  def getAuthenticator: Option[Authenticator] = authenticator

  def getParams: mutable.Map[String, String] = params

  def getObligation: Option[String] = obligation

  def getMessages: mutable.ArrayBuffer[String] = messages
}