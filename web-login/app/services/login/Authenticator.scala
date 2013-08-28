package services.login

import play.api.mvc.{AnyContent, Request}

/**
 */
trait Authenticator {
  /**
   * The method is called by the application to indicate to a authenticator that it is being place into service.
   * The application calls this method exactly once after instantiating the authenticator.
   * The method must be completed successfully before the authenticator is asked to do any work.
   * @param options - map of the options which was specified into the configuration.
   * @return - current instance of the authenticator
   */
  def init(options: Map[String, String]): Authenticator


  /**
   * The method is called each time a login request has been gotten to defines if the authenticator is acceptable
   * to authenticate against the input login context.
   * @param lc - the context of the current authentication process.
   * @param request - the login HTTP request.
   * @return true if the authenticator is acceptable and false otherwise.
   */
  def isYours(implicit lc: LoginContext, request: Request[AnyContent]): Boolean

  /**
   * The method is called each time need to perform the authentication. The result code controls subsequent
   * authentication process (see the result's constants). The following represents a description their respective
   * semantics:
   *    - SUCCESS - if the subject successfully authenticated. If there isn't obligation then the authentication process
   *    continues and all principals will be included in the assertions otherwise it will be performed the obligation
   *    from the current login context. If obligation perform is successfully then the authentication process continues
   *    and all principals will be included in the assertions.
   *    - FAIL - if the subject's credentials are wrong. The authentication process is interrupted. A cause can be added
   *    to the list of the messages.
   *    - PRE_AUTH_REQUIRE - if it requires to do pre-authentication process (e.g: challenge/response protocol).
   *    The client will receive pre_authentication response which contains parameters (e.g: challenge) from the login
   *    context to create the required credentials ans to continue the authentication process.
   * @param lc - context of the current authentication process.
   * @param request - the login HTTP request.
   * @return result code {@see LoginContext}.
   */
  def `do`(implicit lc: LoginContext, request: Request[AnyContent]): Int
}

object Authenticator {

  //Login`s method constants
  val BASIC_METHOD = 1
  val SMART_CARD_METHOD = 2
  val OTP_METHOD = 4

  //Authentication result`s constants
  val SUCCESS = 0
  val FAIL = 1
  val PRE_AUTH_IS_REQUIRE = 2

  def apply(className: String, params: Map[String, String]): Authenticator = {
    new AuthenticatorMeta(className, params).newInstance
  }
}

private[login] class AuthenticatorMeta(private val className: String,
                                       private val options: Map[String, String]) extends Ordered[AuthenticatorMeta]{
  import AuthenticatorMeta._
  import scala.reflect.runtime.{universe => ru}

  private val mirror = ru.runtimeMirror(getClass.getClassLoader)
  private val clsSymbol = mirror.classSymbol(Class.forName(className.replace('>', '.').replaceAll("\"", ""))) //todo: temporary + if authenticators params not set then it will be ignored
  private val clsMirror = mirror.reflectClass(clsSymbol)
  private val clsConstructor = clsMirror.reflectConstructor(clsSymbol.toType.declaration(ru.nme.CONSTRUCTOR).asMethod)

  private val order = options.get(ORDER_PARAM_NAME).fold(Int.MaxValue)(augmentString(_).toInt)

  def newInstance: Authenticator = {
    val instance: Authenticator = clsConstructor().asInstanceOf[Authenticator]
    instance.init(options)
    instance
  }

  def getClassName = className.replace('>', '.').replaceAll("\"", "") //todo: temporary
  def getOptions = options
  def getOrder = order

  def compare(that: AuthenticatorMeta): Int = this.getOrder - that.getOrder

  override def toString: String = {
    val sb =new StringBuilder("AuthenticatorMeta(")
    sb.append("class -> ").append(getClassName)
    sb.append(", ").append("order -> ").append(getOrder)
    sb.append(", ").append("options -> ").append(getOptions)
    sb.append(")").toString()
  }
}

private[login] object AuthenticatorMeta {
  val ORDER_PARAM_NAME = "order"
}

