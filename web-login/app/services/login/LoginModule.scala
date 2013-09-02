package services.login

import play.api.mvc.{AnyContent, Request}

/**
 */
trait LoginModule {

  /**
   * The method is called by the application to indicate to a login module that it is being place into service.
   * The application calls this method exactly once after instantiating the login module.
   * The method must be completed successfully before the login module is asked to do any work.
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
   *    - SUCCESS(0) - if the subject successfully authenticated. All claims will be included in the assertions.
   *    If there is obligation in the login context the controller must performs it successfully before
   *    continue the process. If there are warning in the login context the controller must shows it before
   *    continue the process.
   *    - PARTIALLY_COMPLETED(2) - if the authentication process is completed partially. In this case the
   *    login process stays at the current step. The next request with the same authentication method will be
   *    processed by the same login module. The login module can store the specific state of authentication process
   *    by the parameters of the current login context.
   *    - FAIL(1) - if the subject's authentication is failed. The authentication process is interrupted.
   *    A cause can be added to the list of the login context's errors.
   * @param lc - context of the current authentication process.
   * @param request - the login HTTP request.
   * @return result code: 0 - Success, 1 - Fail, 2 - Partially completed.
   */
  def `do`(implicit lc: LoginContext, request: Request[AnyContent]): Int
}

object LoginModule {
  object Result extends Enumeration {
    type Result = Value

    val SUCCESS = Value(0)
    val FAIL = Value(1)
    val PARTIALLY_COMPLETED = Value(2)
  }

  def apply(className: String, params: Map[String, String]): LoginModule = {
    new LoginModuleMeta(className, params).newInstance
  }
}

private[login] class LoginModuleMeta(private val className: String,
                                       private val options: Map[String, String]) extends Ordered[LoginModuleMeta]{
  import LoginModuleMeta._
  import scala.reflect.runtime.{universe => ru}

  private val mirror = ru.runtimeMirror(getClass.getClassLoader)
  private val clsSymbol = mirror.classSymbol(Class.forName(className.replace('>', '.').replaceAll("\"", ""))) //todo: temporary + if login modules params not set then it will be ignored
  private val clsMirror = mirror.reflectClass(clsSymbol)
  private val clsConstructor = clsMirror.reflectConstructor(clsSymbol.toType.declaration(ru.nme.CONSTRUCTOR).asMethod)

  private val order = options.get(ORDER_PARAM_NAME).fold(Int.MaxValue)(augmentString(_).toInt)

  def newInstance: LoginModule = {
    val instance: LoginModule = clsConstructor().asInstanceOf[LoginModule]
    instance.init(options)
    instance
  }

  def getClassName = className.replace('>', '.').replaceAll("\"", "") //todo: temporary
  def getOptions = options
  def getOrder = order

  def compare(that: LoginModuleMeta): Int = this.getOrder - that.getOrder

  override def toString: String = {
    val sb =new StringBuilder("LoginModuleMeta(")
    sb.append("class -> ").append(getClassName)
    sb.append(", ").append("order -> ").append(getOrder)
    sb.append(", ").append("options -> ").append(getOptions)
    sb.append(")").toString()
  }
}

private[login] object LoginModuleMeta {
  val ORDER_PARAM_NAME = "order"
}

