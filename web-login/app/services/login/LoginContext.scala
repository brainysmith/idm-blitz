package services.login

import com.blitz.idm.app.json.{JVal, JObj}
import play.api.mvc.{AnyContent, Request, Call}
import LoginErrors._
import play.api.i18n.Messages

/**
 * Context's interface of the authentication process.
 */
trait LoginContext {
  import scala.Predef._

  /**
   * Retrieve the current login method of the authentication process.
   * See the login's method constants of the current interface.
   * @return code of the login method.
   */
  def getMethod: Int

  /**
   * Retrieve login methods that have been successfully completed.
   * See the login's method constants of the current interface.
   * @return the bitmask of the login methods.
   */
  def getCompletedMethods: Option[Int]

  /**
   * Retrieve of the current status of the authentication process.
   * See the login's status constants of the current interface.
   * @return the current status of the login process.
   */
  def getStatus: Int

  /**
   * Retrieve the array of credentials with associated with the current login request.
   * After the request has been processed credentials will be discarding (they are not serialized) and
   * the next login request with this login context it wouldn't be able to get one.
   * @return array of the credentials associated with current http request.
   */
  def getCredentials: Seq[Credentials]

  /**
   * Appends the specified credential to the credential's array.
   * @return the current login context.
   */
  def withCredentials(credentials: Credentials): LoginContext

  /**
   * Retrieve the login module which was selected in the current login context.
   * @return None if an login module has not yet been selected and some login module otherwise.
   */
  def getLoginModule: Option[LoginModule]

  /**
   * Set of the claims about the current subject.
   * @return json with claims
   */
  def getClaims: JObj

  /**
   * Add a specified claim to the current set of the claims.
   * @param claim the tuple with the claim name and value.
   * T type of the claim value, for example: Int, Boolean, String and others type of the JSON.
   * @return the current login context.
   */
  def withClaim(claim: (String, JVal)): LoginContext

  /**
   * Add a specified claims to the current set of the claims.
   * @param claims the json object with additional claims.
   * @return the current login context.
   */
  def withClaims(claims: JObj): LoginContext

  /**
   * Retrieve the parameter's map which associated with the current authentication process.
   * If there is no parameters returns the empty map.
   * Each parameter has a name and a value.
   * @return map with parameters.
   */
  def getParams: JObj

  /**
   * Adds a specified parameter to the current set of the parameters.
   * @param param the tuple with the parameter name and value.
   * T type of the parameter value, for example: Int, Boolean, String and others type of the JSON.
   * @return the current login context.
   */
  def withParam(param: (String, JVal)): LoginContext

  /**
   * Add a specified parameters to the current set of the parameters (merge).
   * @param params the json object with additional parameters.
   * @return the current login context.
   */
  def withParams(params: JObj): LoginContext

  /**
   * Returns the obligation which must be executed before the authentication process will be continued.
   * After the request has been processed the obligation will be discarding (it is not serialized) and
   * the next login request with this login context it wouldn't be able to get one.
   * @return a string representing the obligation.
   */
  def getObligation: Option[String]

  /**
   * Sets a specified obligation for the current login context.
   * @param obligation the name of the obligation.
   * @return the current login context.
   */
  def withObligation(obligation: String): LoginContext

  /**
   * Retrieve all errors associated with the current login context and request.
   * After the request has been processed the all errors will be discarding (they are not serialized) and
   * the next login request with this login context it wouldn't be able to get one.
   * @return list of the tuple with error's key and error's localized message.
   * */
  def getErrors: Seq[(String, String)]

  /**
   * Retrieve an error's localized message for the specified error's key.
   * @param key error's key.
   * @return error's localized message.
   * */
  def getError(key: String): Option[String]

  /**
   * Appends an error to the error's array of the current login context.
   * @param key the error's key.
   * @param msg the error's localized message.
   * @return the current login context.
   */
  def withError(key: String, msg: String): LoginContext


  /**
   * Appends a common login error to the error's array of the current login context.
   * The error's key will be: LoginErrors + "." + common login error's name.
   * @param error the common login error.
   * @param msg the localized error's message.
   * @return the current login context.
   */
  def withError(error: LoginErrors, msg: String): LoginContext

  /**
   * Appends a common login error to the error's array of the current login context.
   * The error's key will be: LoginErrors + "." + common login error's name.
   * The error's localized message will be got from the internal bundle.
   * @param error - common login error.
   * @param args the error arguments.
   * @return the current login context.
   */
  def withError(error: LoginErrors, args: Any*)(implicit request: Request[AnyContent]): LoginContext


  /**
   * Array of the warnings associated with the current login context and request.
   * After the request has been processed the all warnings will be discarding (they are not serialized) and
   * the next login request with this login context it wouldn't be able to get one.
   * @return list of the tuple with warning's key and warning's localized message.
   * */
  def getWarns: Seq[(String, String)]

  /**
   * Appends a warn to the warnings array of the current login context.
   * @param key the warning's key.
   * @param msg the warning's localized message.
   * @return the current login context.
   */
  def withWarn(key: String, msg: String): LoginContext
}

object LoginContext {
  import LoginModule._
  import play.api.mvc.{AnyContent, Request}

  //Login`s status constants
  val INIT_STATUS = -1
  val AUTH_SUCCESS_STATUS = 0
  val AUTH_FAIL_STATUS = 1
  val DO_PRE_AUTH_STATUS = 2

  def basic(lgnAndPwd: (String, String)): LoginContext = {
    new LoginContextImpl(BASIC_METHOD) withCredentials Credentials(lgnAndPwd)
  }

  def apply(method: Int)(implicit request : Request[AnyContent]): LoginContext = {
    new LoginContextImpl(method)
  }
}

private[login] class LoginContextImpl(private val method: Int) extends LoginContext {
  import scala.collection.mutable
  import play.api.mvc.Call
  import LoginContext._
  import LoginErrors._

  private var completedMethods: Option[Int] = None
  private var status = INIT_STATUS
  private val crls = new mutable.ArrayBuffer[Credentials]()
  private var loginModule: Option[LoginModule] = None

  private var claims = JObj(Seq())
  private var params = JObj(Seq())

  //it isn't serialized
  private var obligation: Option[String] = None
  private val errors = new mutable.ArrayBuffer[(String, String)]()
  private val warns = new mutable.ArrayBuffer[(String, String)]()


  override def getStatus: Int = status

  override def getMethod: Int = method

  override def getCompletedMethods: Option[Int] = completedMethods

  override def getLoginModule: Option[LoginModule] = loginModule

  override def getCredentials: Seq[Credentials] = crls.toArray

  override def withCredentials(credentials: Credentials): LoginContext = {
    crls += credentials
    this
  }

  override def getClaims: JObj = claims

  override def withClaim(claim: (String, JVal)): LoginContext = {
    claims = claims +! claim
    this
  }

  override def withClaims(claims: JObj): LoginContext = {
    this.claims = this.claims ++! claims
    this
  }

  override def getParams: JObj = params

  override def withParam(param: (String, JVal)): LoginContext = {
    params = params +! param
    this
  }

  override def withParams(params: JObj): LoginContext = {
    this.params = this.params ++! params
    this
  }

  override def getObligation: Option[String] = obligation

  override def withObligation(obligation: String): LoginContext = {
    this.obligation = Option(obligation)
    this
  }

  override def getErrors: Seq[(String, String)] = errors

  override def getError(key: String): Option[String] = errors.find(_._1 == key).map(error => error._2)

  override def withError(key: String, msg: String): LoginContext = {
    errors += ((key, msg))
    this
  }

  override def withError(error: LoginErrors, msg: String): LoginContext = {
    errors += ((error.getKey, msg))
    this
  }

  override def withError(error: LoginErrors, args: Any*)(implicit request: Request[AnyContent]): LoginContext = {
    val key = error.getKey
    errors += ((key, Messages(key, args)))
    this
  }

  override def getWarns: Seq[(String, String)] = warns

  override def withWarn(key: String, msg: String): LoginContext = {
    warns += ((key, msg))
    this
  }


  //additional functions
  def setStatus(iStatus: Int): LoginContextImpl = {
    status = iStatus
    this
  }

  def setLoginModule(loginModule: LoginModule): LoginContextImpl = {
    this.loginModule = Option(loginModule)
    this
  }

  //todo: check is it possible
  def clearLoginModule: LoginContextImpl = {
    loginModule = None
    this
  }


  override def toString: String = {
    val sb =new StringBuilder("LoginContextImpl(")
    sb.append("method -> ").append(method)
    sb.append("completedMethod -> ").append(completedMethods)
    sb.append(", ").append("status -> ").append(status)
    sb.append(", ").append("credentials -> ").append(crls.toList)
    sb.append(", ").append("loginModule -> ").append(loginModule)
    sb.append(", ").append("claims -> ").append(claims.toJson)
    sb.append(", ").append("parameters -> ").append(params.toJson)
    sb.append(", ").append("obligation -> ").append(obligation)
    sb.append(", ").append("errors -> ").append(errors.toList)
    sb.append(", ").append("warns -> ").append(warns.toList)
    sb.append(")").toString()
  }
}