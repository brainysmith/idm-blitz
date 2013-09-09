package services.login

import com.blitz.idm.app._
import com.blitz.idm.app.json._
import play.api.mvc.{AnyContent, Request}
import play.api.i18n.Messages

/**
 * Implementation of the login context.
 */
/*todo: make serialization*/
class LoginContextImpl extends LoginContext {
  import scala.collection.mutable

  private var currentMethod: Option[Int] = None
  private var completedMethods: Option[Int] = None
  private var status: LoginStatus = LoginStatus.INITIAL

  private var claims = JObj(Seq())
  private var params = JObj(Seq())

  private val lmsToProcess = new mutable.ArrayBuffer[LoginModule]()
  private var loginModule: Option[LoginModule] = None

  //it isn't serialized
  private val crlsArray = new mutable.ArrayBuffer[JObj]()
  private var obligation: Option[Any] = None
  private val errors = new mutable.ArrayBuffer[(String, String)]()
  private val warns = new mutable.ArrayBuffer[(String, String)]()


  override def getStatus: LoginStatus = status

  override def getCurrentMethod: Option[Int] = currentMethod

  override def getCompletedMethods: Option[Int] = completedMethods

  override def getLoginModule[B <: LoginModule]: Option[B] = {
    loginModule.map[B](_.asInstanceOf[B])
  }

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

  def getObligation: Option[Any] = obligation

  def withObligation(obligation: Any): LoginContext = {
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

  def clearLoginModulesToProcess: LoginContextImpl = {
    lmsToProcess.clear()
    this
  }

  /*todo: change it*/
  def toJson: String = {
    val jObj: JObj = Json.obj("status" -> getStatus.name,
             //"lmsToProcess" -> Json.arr(getLoginModulesToProcess().map(_.getClass.getSimpleName)),
             "params" -> getParams,
             "claims" -> getClaims)

    getCurrentMethod.map(method => jObj + ("curMethod" -> method))
    getCompletedMethods.map(methods => jObj + ("completedMethod" -> methods))
    getLoginModule[LoginModule].map(lm => jObj + ("lm" -> lm.getClass.getSimpleName))

    jObj.toJson
  }

  override def toString: String = {
    val sb =new StringBuilder("LoginContextImpl(")
    sb.append("currentMethod -> ").append(currentMethod)
    sb.append(", ").append("completedMethod -> ").append(completedMethods)
    sb.append(", ").append("status -> ").append(status)
    sb.append(", ").append("loginModulesToProcess -> ").append(lmsToProcess.toList)
    sb.append(", ").append("claims -> ").append(claims.toJson)
    sb.append(", ").append("parameters -> ").append(params.toJson)
    sb.append(", ").append("loginModule -> ").append(loginModule)
    sb.append(", ").append("obligation -> ").append(obligation)
    sb.append(", ").append("errors -> ").append(errors.toList)
    sb.append(", ").append("warns -> ").append(warns.toList)
    sb.append(")").toString()
  }
}

/*todo: add private*/
object LoginContextImpl {

  def fromJson(json: String): LoginContextImpl = {
    val jObj: JObj = JVal.parseStr(json).asInstanceOf[JObj]
    val lc = new LoginContextImpl()

    lc.status = LoginStatus.valueOf(jObj("status").as[String])
    lc.params = jObj("params").asInstanceOf[JObj]
    lc.claims = jObj("claims").asInstanceOf[JObj]

    lc.currentMethod = jObj("curMethod").asOpt[Int]
    lc.completedMethods = jObj("completedMethod").asOpt[Int]

    jObj("lm").asOpt[String].fold(lc.loginModule = None)(lmName => {
      LoginManager.loginModules.find(_.getClass.getSimpleName == lmName).fold[Unit]({
        //todo: throw exception
        appLogError("the deserialization fail: can't find specified login module [name = {}]", lmName)
        throw new IllegalArgumentException("the deserialization fail: can't find specified login module")
      })(lm => {lc.loginModule = Some(lm)})
    })

    //todo: lmsToProcess
    //jObj("loginModulesToProcess").as
    lc
  }
}