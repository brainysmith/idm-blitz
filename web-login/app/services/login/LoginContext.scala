package services.login

import com.blitz.idm.app.json.{JWriter, JStr, JObj}
import play.api.mvc.{AnyContent, Request}
import play.api.i18n.Messages

/**
 * Context's interface of the authentication process.
 */
trait LoginContext {
  import scala.Predef._

  /**
   * Retrieve the current authentication method of the login process.
   * See the authentication's methods enum.
   * @return the identifier of the current authentication method.
   */
  def getCurrentMethod: Option[Int]

  /**
   * Retrieve login methods that have been successfully completed.
   * See the authentication's methods enum.
   * @return the bitmask of the login methods.
   */
  def getCompletedMethods: Option[Int]

  /**
   * Retrieve of the current status of the authentication process.
   * @return the current status of the login process.
   */
  def getStatus: LoginStatus

  /**
   * Retrieve the array of credentials with associated with the current login request.
   * After the request has been processed credentials will be discarding (they are not serialized) and
   * the next login request with this login context it wouldn't be able to get one.
   * @return array of the credentials associated with current http request.
   */
  def getCredentials: Seq[JObj]

  /**
   * Appends the specified credential to the credential's array.
   * @param crls the credentials which must be added.
   * @return the current login context.
   */
  def withCredentials(crls: JObj): LoginContext

  /**
   * Set of the claims about the current subject.
   * @return json with claims
   */
  def getClaims: JObj

  /**
   * Add a specified claim to the current set of the claims.
   * @param claim the tuple with the claim name and value.
   * @tparam T type of the claim value, for example: Int, Boolean, String and others type of the JSON.
   * @return the current login context.
   */
  def withClaim[T](claim: (String, T))(implicit writer: JWriter[T]): LoginContext

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
   * @tparam T type of the parameter value, for example: Int, Boolean, String and others type of the JSON.
   * @return the current login context.
   */
  def withParam[T](param: (String, T))(implicit writer: JWriter[T]): LoginContext

  /**
   * Add a specified parameters to the current set of the parameters (merge).
   * @param params the json object with additional parameters.
   * @return the current login context.
   */
  def withParams(params: JObj): LoginContext

  /**
   * Retrieve the login module which has performed authentication successfully.
   * @return None if an authentication has not performed yet for the current authentication method and some login
   *         module otherwise.
   */
  def getLoginModule: Option[LoginModule]

  /**
   * Returns the obligation which must be executed before the authentication process will be continued.
   * After the request has been processed the obligation will be discarding (it is not serialized) and
   * the next login request with this login context it wouldn't be able to get one.
   * @return None if there isn't obligation ans some string representing the obligation otherwise.
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
   * The error's key will be: "BuildIn." + common login error's name.
   * @param error the common login error.
   * @param msg the localized error's message.
   * @return the current login context.
   */
  def withError(error: BuildInError, msg: String): LoginContext

  /**
   * Appends a common login error to the error's array of the current login context.
   * The error's key will be: LoginErrors + "." + common login error's name.
   * The error's localized message will be got from the internal bundle.
   * @param error - common login error.
   * @param args the error arguments.
   * @return the current login context.
   */
  def withError(error: BuildInError, args: Any*)(implicit request: Request[AnyContent]): LoginContext


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

  def basic(lgnAndPwd: (String, String)): LoginContext = {
    LoginContext() withCredentials JObj(Seq("lgn" -> JStr(lgnAndPwd._1), "pswd" -> JStr(lgnAndPwd._2)))
  }

  def apply(): LoginContext = {
    new LoginContextImpl()
  }
}

private[login] class LoginContextImpl extends LoginContext {
  import scala.collection.mutable

  private var currentMethod: Option[Int] = None
  private var completedMethods: Option[Int] = None
  private var status: LoginStatus = LoginStatus.INITIAL

  private var claims = JObj(Seq())
  private var params = JObj(Seq())

  private val lmsToProcess = new mutable.ArrayBuffer[LoginModule]()

  //it isn't serialized
  private val crlsArray = new mutable.ArrayBuffer[JObj]()
  private var loginModule: Option[LoginModule] = None
  private var obligation: Option[String] = None
  private val errors = new mutable.ArrayBuffer[(String, String)]()
  private val warns = new mutable.ArrayBuffer[(String, String)]()


  override def getStatus: LoginStatus = status

  override def getCurrentMethod: Option[Int] = currentMethod

  override def getCompletedMethods: Option[Int] = completedMethods

  override def getLoginModule: Option[LoginModule] = loginModule

  override def getCredentials: Seq[JObj] = crlsArray.toArray[JObj]

  override def withCredentials(crls: JObj): LoginContext = {
    crlsArray += crls
    this
  }

  override def getClaims: JObj = claims

  override def withClaim[T](claim: (String, T))(implicit writer: JWriter[T]): LoginContext = {
    claims = claims +! claim
    this
  }

  override def withClaims(claims: JObj): LoginContext = {
    this.claims = this.claims ++! claims
    this
  }

  override def getParams: JObj = params

  override def withParam[T](param: (String, T))(implicit writer: JWriter[T]): LoginContext = {
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

  override def getErrors: Seq[(String, String)] = errors.toSeq

  override def getError(key: String): Option[String] = errors.find(_._1 == key).map(error => error._2)

  override def withError(key: String, msg: String): LoginContext = {
    errors += ((key, msg))
    this
  }

  override def withError(error: BuildInError, msg: String): LoginContext = {
    errors += ((error.getKey, msg))
    this
  }

  override def withError(error: BuildInError, args: Any*)(implicit request: Request[AnyContent]): LoginContext = {
    val key = error.getKey
    errors += ((key, Messages(key, args)))
    this
  }

  override def getWarns: Seq[(String, String)] = warns.toSeq

  override def withWarn(key: String, msg: String): LoginContext = {
    warns += ((key, msg))
    this
  }


  //additional functions
  def setCurrentMethod(method: Int): LoginContextImpl = {
    this.currentMethod = Some(method)
    this
  }

  def setStatus(eStatus: LoginStatus): LoginContextImpl = {
    status = eStatus
    this
  }

  def setLoginModule(loginModule: LoginModule): LoginContextImpl = {
    this.loginModule = Some(loginModule)
    this
  }

  /*todoL thinking about it, maybe enough that it will not be serialize */
  def clearLoginModule: LoginContextImpl = {
    this.loginModule = None
    this
  }

  def getLoginModulesToProcess(): Seq[LoginModule] = lmsToProcess.toSeq

  def addLoginModuleToProcess(lm: LoginModule): LoginContextImpl = {
    lmsToProcess += lm
    this
  }

  def clearLoginModulesToProcess(): LoginContextImpl = {
    lmsToProcess.clear()
    this
  }

  override def toString: String = {
    val sb =new StringBuilder("LoginContextImpl(")
    sb.append("currentMethod -> ").append(currentMethod)
    sb.append(", ").append("completedMethod -> ").append(completedMethods)
    sb.append(", ").append("status -> ").append(status)
    sb.append(", ").append("loginModulesToProcess -> ").append(lmsToProcess.toList)
    sb.append(", ").append("credentials -> ").append(crlsArray.toList)
    sb.append(", ").append("claims -> ").append(claims.toJson)
    sb.append(", ").append("parameters -> ").append(params.toJson)
    sb.append(", ").append("loginModule -> ").append(loginModule)
    sb.append(", ").append("obligation -> ").append(obligation)
    sb.append(", ").append("errors -> ").append(errors.toList)
    sb.append(", ").append("warns -> ").append(warns.toList)
    sb.append(")").toString()
  }
}