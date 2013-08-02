package services.login

import play.api.mvc.{AnyContent, Request}
import scala.collection.mutable
import java.security.Principal

/**
 * Interface of the authentication process.
  */
trait LoginContext {

  /**
   * Return the current HTTP request.
   * @return request.
   */
  def getRequest: Request[AnyContent]

  /**
   * Returns the current login method of the authentication process.
   * See the login's method constants of the current interface.
   * @return code of the login method.
   */
  def getMethod: Int

  /**
   * Returns the array of credentials with associated with the current login request.
   * After a http request has been proceeded the array of credentials is cleared.
   * @return array of the credentials associated with current http request.
   */
  def getCredentials: Array[Credentials]

  /**
   * Appends the specified credential to the credential's array.
   * @return the current login context.
   */
  def +(credentials: Credentials): LoginContext

  /**
   * Returns the array of principals with associated with the current login request.
   * After a http request has been proceeded the array of a principal is cleared.
   * @return array of the principals associated with current http request.
   */
  def getPrincipals: Array[Principal]

  /**
   * Appends the specified principal to the credential's array.
   * @return the current login context.
   */
  def +(principal: Principal): LoginContext

  /**
   * Returns of the current status of the authentication process.
   * See the login's status constants of the current interface.
   * @return the current status of the login process.
   */
  def getStatus: Int

  /**
   * Returns login methods that have been successfully completed.
   * See the login's method constants of the current interface.
   * @return the bitmask of the login methods.
   */
  def getCompletedMethods: Option[Int]

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
  def getParams: Map[String, String]

  /**
   * Appends the parameter to the parameter's array.
   * @return the current login context.
   */
  def +(param: (String, String)): LoginContext

  /**
   * Returns the obligation which must be executed before the authentication process will be completed.
   * @return string representing the obligation.
   */
  def getObligation: Option[String]

  /**
   * Array of the message's keys which will be sent to the client.
   * After message has been shown it will be deleted from the array.
   * @return array of the message's key.
   * */
  def getMessages: Array[String]

  /**
   * Appends the message to the message's array.
   * @return the current login context.
   */
  def +(msg: String): LoginContext
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

  def basic(lgnAndPwd: (String, String))(implicit request : Request[AnyContent]): LoginContext = {
    new LoginContextImpl(BASIC_METHOD) + Credentials(lgnAndPwd)
  }

  def apply(method: Int)(implicit request : Request[AnyContent]): LoginContext = {
    new LoginContextImpl(method)
  }
}

private class LoginContextImpl(private val method: Int)(implicit val request: Request[AnyContent]) extends LoginContext {
  import LoginContext._

  private val params = new mutable.HashMap[String, String]()
  private val msgs = new mutable.ArrayBuffer[String]()
  private val crls = new mutable.ArrayBuffer[Credentials]()
  private val prls = new mutable.ArrayBuffer[Principal]()

  private var status = INIT_STATUS
  private var completedMethods: Option[Int] = None
  private var authenticator: Option[Authenticator] = None
  private var obligation: Option[String] = None

  def getRequest: Request[AnyContent] = request

  def getStatus: Int = status

  def getMethod: Int = method

  def getCompletedMethods: Option[Int] = completedMethods

  def getAuthenticator: Option[Authenticator] = authenticator

  def getCredentials: Array[Credentials] = crls.toArray

  def +(credentials: Credentials): LoginContext = {
    crls += credentials
    this
  }

  def getParams: Map[String, String] = params.toMap

  def +(param: (String, String)): LoginContext = {
    params += param
    this
  }

  def getObligation: Option[String] = obligation

  def getMessages: Array[String] = msgs.toArray

  def +(msg: String): LoginContext = {
    msgs += msg
    this
  }

  def getPrincipals: Array[Principal] = prls.toArray

  def +(principal: Principal): LoginContext = {
    prls += principal
    this
  }
}