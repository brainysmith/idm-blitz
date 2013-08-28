package services.login

import play.api.mvc.{Call, AnyContent, Request}
import scala.collection.mutable
import java.security.Principal

/**
 * Interface of the authentication process.
  */
trait LoginContext {
  import LoginErrors._

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
  def getObligation: Option[Call]

  /**
   * Array of the message's keys associated with the current login context. In common case it's errors.
   * After message has been shown it will be deleted from the array.
   * @return array of the message's key.
   * */
  def getMessages: Array[String]

  /**
   * Appends the message to the message's array of the current login context.
   * @param msg - message's key which will be able to the login controller.
   * @return the current login context.
   */
  def +(msg: String): LoginContext


  /**
   * Appends the common login error to the message's array of the current login context.
   * The message key is defined as: LoginErrors + "." + error's name.
   * @param error - common login error.
   * @return the current login context
   */
  def +(error: LoginErrors): LoginContext
}

object LoginContext {
  import Authenticator._

  //Login`s status constants
  val INIT_STATUS = -1
  val AUTH_SUCCESS_STATUS = 0
  val AUTH_FAIL_STATUS = 1
  val DO_PRE_AUTH_STATUS = 2
  val DO_OBLIGATION_STATUS = 3

  def basic(lgnAndPwd: (String, String)): LoginContext = {
    new LoginContextImpl(BASIC_METHOD) + Credentials(lgnAndPwd)
  }

  def apply(method: Int)(implicit request : Request[AnyContent]): LoginContext = {
    new LoginContextImpl(method)
  }
}

private[login] class LoginContextImpl(private val method: Int) extends LoginContext {
  import LoginContext._
  import LoginErrors._

  private val params = new mutable.HashMap[String, String]()
  private val msgs = new mutable.ArrayBuffer[String]()
  private val crls = new mutable.ArrayBuffer[Credentials]()
  private val prls = new mutable.ArrayBuffer[Principal]()

  private var status = INIT_STATUS
  private var authenticator: Option[Authenticator] = None

  //todo: thinking about it
  private var completedMethods: Option[Int] = None
  private var obligation: Option[Call] = None

  def getStatus: Int = status
  def setStatus(iStatus: Int): LoginContextImpl = {
    status = iStatus
    this
  }

  def getMethod: Int = method
  def getCompletedMethods: Option[Int] = completedMethods

  def getAuthenticator: Option[Authenticator] = authenticator
  def setAuthenticator(iAuthenticator: Authenticator): LoginContextImpl = {
    authenticator = Option(iAuthenticator)
    this
  }
  def clearAuthenticator: LoginContextImpl = {
    authenticator = None
    this
  }

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

  def getObligation: Option[Call] = obligation

  def getMessages: Array[String] = msgs.toArray

  def +(msg: String): LoginContext = {
    msgs += msg
    this
  }

  def +(error: LoginErrors): LoginContext = {
    msgs += "LoginErrors." + error.toString
    this
  }

  def getPrincipals: Array[Principal] = prls.toArray

  def +(principal: Principal): LoginContext = {
    prls += principal
    this
  }

  override def toString: String = {
    val sb =new StringBuilder("LoginContextImpl(")
    sb.append("method -> ").append(method)
    sb.append(", ").append("status -> ").append(status)
    sb.append(", ").append("authenticator -> ").append(authenticator)
    sb.append(", ").append("credentials -> ").append(crls.toList)
    sb.append(", ").append("parameters -> ").append(params.toMap)
    sb.append(", ").append("obligation -> ").append(obligation)
    sb.append(", ").append("messages -> ").append(msgs.toList)
    sb.append(", ").append("principals -> ").append(prls.toList)
    sb.append(")").toString()
  }
}