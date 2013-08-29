package services.login

import play.api.mvc.{AnyContent, Request}
import com.blitz.idm.app._
import LoginModule._

import com.unboundid.util.ssl.{TrustAllTrustManager, SSLUtil}
import com.unboundid.ldap.sdk._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import com.unboundid.ldap.sdk.controls.{PasswordExpiredControl, PasswordExpiringControl}

/**
  */
class LdapBasicLoginModule extends LoginModule {
  import LdapBasicLoginModule._

  private var options: Map[String, String] = _
  private var pool: LDAPConnectionPool = _

  //configurable options //todo: thinking about it
  private var host: String = _
  private var port: Int = _
  private var useSSL: Boolean = _
  private var autoReconnect: Boolean = _
  private var initialConnections: Int = _
  private var maxConnections: Int = _
  private var userDnPattern: String = _



  //todo: change implementation
  @Override
  def init(options: Map[String, String]): LoginModule = {
    appLogTrace("initializing the ldap authenticator [options={}]", options)


    host = options.getOrElse("host", null)
    port = 1636
    useSSL = true
    autoReconnect = true

    var connection: LDAPConnection = null
    val connectionOption = new LDAPConnectionOptions()
    connectionOption.setAutoReconnect(autoReconnect)
    if (useSSL) {
      val sslUtil = new SSLUtil(new TrustAllTrustManager())
      connection = new LDAPConnection(sslUtil.createSSLSocketFactory(), connectionOption, host, port)
    } else {
      connection = new LDAPConnection(connectionOption, host, port)
    }


    initialConnections = 1
    maxConnections = 3
    pool = new LDAPConnectionPool(connection, initialConnections, maxConnections)


    userDnPattern = options.getOrElse("userDn", null)

    this.options = options
    this
  }

  @Override
  def isYours(implicit lc: LoginContext, request: Request[AnyContent]): Boolean = {
    lc.getMethod == BASIC_METHOD
  }

  @Override
  def `do`(implicit lc: LoginContext, request: Request[AnyContent]): Int = {
    appLogTrace("attempting to authenticate user by LDAP server [login context: {}]", lc)

    //perform login for all credentials

    Try(pool.getConnection) match {
      case Success(connection) => {
        appLogTrace("got a ldap connection from the pool")

        val authRes = lc.getCredentials.foldLeft(FAIL)((prevRes, crl) => {
          (prevRes, crl) match {
            case (FAIL, basicCrl: BasicCredentials) => {
                  val userDn = interpolate(userDnPattern, Map("USERNAME" -> basicCrl.lgn))
                  val tEntry = for {
                    tBind <- Try({
                      appLogTrace("try to bind to LDAP server [userDn: {}]", userDn)
                      val bindReq = new SimpleBindRequest(userDn, basicCrl.pswd)
                      connection.bind(bindReq)
                    })
                    dummy <- Try({
                      if (!tBind.getResultCode.isConnectionUsable) {
                        appLogError("cannot bind to LDAP server: the connection isn't usable [resultCode: {}]", tBind.getResultCode)
                        throw new RuntimeException("cannot bind to LDAP server: the connection isn't usable")
                      }
                    })
                    tEntry <- Try({
                      appLogTrace("getting the user's entry [userDn: {}]", userDn)

                      //todo: add needed attributes
                      connection.getEntry(userDn)
                    })
                  } yield tEntry

                  //analise the result
                  tEntry match {
                    case Success(resEntity) => {
                      //todo: add user attributes to lc

                      val authRes = SUCCESS
                      resEntity.getControls.foreach(control => {
                        appLogTrace("got a control [name={}, oid={}]", control.getControlName, control.getOID)
                        control match {
                          case expiringControl: PasswordExpiringControl => {
                            appLogTrace("the user's password will expire in the near future [after {} sec]",
                              expiringControl.getSecondsUntilExpiration)

                            //todo: do it
                            throw new UnsupportedOperationException("hasn't realized yet")
                          }
                          case expiredControl: PasswordExpiredControl => {
                            appLogTrace("the user's password has expired")

                            //todo: do it
                            throw new UnsupportedOperationException("hasn't realized yet")
                          }
                          case _ => {

                          }
                        }
                      })

                      appLogDebug("authentication by LDAP server is successful [credentials: {}]", basicCrl)
                      authRes
                    }
                    case Failure(e) => {
                      e match {
                        case le: LDAPException => {
                          appLogDebug("authentication by LDAP server fails [credentials: {}, error: {}]", basicCrl, le)

                          //todo: check the controls

                          errorMapper.get(le.getResultCode).fold({
                            lc + (UNMAPPED_ERROR_MSG_PREFIX + le.getResultCode)
                          })(lc +)
                          FAIL
                        }
                        case _ => {
                          appLogError("can't perform authentication be LDAP server. Unknown error has occurred: {}", e)
                          throw e
                        }
                      }
                    }
                  }
            }
            case _ => prevRes
          }
        })

        //release the connection
        pool.releaseConnection(connection)
        authRes
      }
      case Failure(e) => {
        //can't get connection
        appLogError("can't get a ldap connection from the pool")
        throw e
      }
    }

  }

  private def interpolate(text: String, vars: Map[String, String]) =
    (text /: vars) { (t, kv) => t.replace("${"+kv._1+"}", kv._2)  }

  override def toString: String = {
    val sb =new StringBuilder("LdapAuthenticator(")
    sb.append("options -> ").append(options)
    sb.append(")").toString()
  }
}

private object LdapBasicLoginModule {
  val UNMAPPED_ERROR_MSG_PREFIX = "LdapBasicAuthenticator.error."

  //todo: add mapper for password expired
  val errorMapper = Map(ResultCode.NO_SUCH_OBJECT -> LoginErrors.NO_USER_FOUND,
                        ResultCode.INVALID_CREDENTIALS -> LoginErrors.INVALID_CREDENTIALS,
                        ResultCode.UNWILLING_TO_PERFORM -> LoginErrors.ACCOUNT_IS_LOCKED)

}
