package services.login

import play.api.mvc.{AnyContent, Request}
import com.blitz.idm.app._

import com.unboundid.util.ssl.{TrustAllTrustManager, SSLUtil}
import com.unboundid.ldap.sdk._
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import com.unboundid.ldap.sdk.controls.{PasswordExpiredControl, PasswordExpiringControl}
import play.api.i18n.Messages

/**
  */
class LdapLoginModule extends BasicLoginModule {
  import LdapLoginModule._

  private var options: Map[String, String] = _
  private var pool: LDAPConnectionPool = null

  //configurable options //todo: thinking about it
  private var host: String = _
  private var port: Int = _
  private var useSSL: Boolean = _
  private var autoReconnect: Boolean = _
  private var initialConnections: Int = _
  private var maxConnections: Int = _
  private var userDnPattern: String = _


  //todo: change implementation
  override def init(options: Map[String, String]): LoginModule = {
    appLogTrace("initializing the ldap login module [options={}]", options)

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

  override def start(implicit lc: LoginContext, request: Request[AnyContent]): Boolean = {
    lc.getCurrentMethod.fold(false)(_ == BuildInMethods.BASIC.id)
  }

  override def `do`(implicit lc: LoginContext, request: Request[AnyContent]): Result = {
    appLogTrace("attempting to authenticate subject by LDAP server [login context: {}]", lc)
    Result.FAIL
    //perform login for all credentials

    Try(pool.getConnection) match {
      case Success(connection) => {
        appLogTrace("got a ldap connection from the pool")

        val authRes = lc.getCredentials.foldLeft[Result](Result.FAIL)((authRes, crl) => {
          if (authRes != Result.FAIL) {
            authRes
          } else {
            (crl("lgn").asOpt[String], crl("pswd").asOpt[String]) match {
              case (Some(lgn), Some(pswd)) => {
                val userDn = interpolate(userDnPattern, Map("USERNAME" -> lgn))
                val tEntry = for {
                  tBind <- Try({
                    appLogTrace("try to bind to LDAP server [userDn: {}]", userDn)
                    val bindReq = new SimpleBindRequest(userDn, pswd)
                    connection.bind(bindReq)
                  })
                  dummy <- Try({
                    if (!tBind.getResultCode.isConnectionUsable) {
                      appLogError("cannot bind to LDAP server: the connection isn't usable [resultCode: {}]",
                        tBind.getResultCode)
                      throw new RuntimeException("cannot bind to LDAP server: the connection isn't usable")
                    } else {
                      appLogDebug("bind to LDAP server is successful [userDn: {}]", userDn)
                      //check the response controls
                      tBind.getResponseControls.foreach(control => {
                        appLogTrace("got a control [name={}, oid={}]", control.getControlName, control.getOID)
                        control match {
                          case expiringControl: PasswordExpiringControl => {
                            appLogTrace("the subject's password will expire in the near future [after {} sec]",
                              expiringControl.getSecondsUntilExpiration)
                            lc.withWarn(NEAR_PSWD_EXPIRE_WARN_KEY, Messages(NEAR_PSWD_EXPIRE_WARN_KEY,
                              expiringControl.getSecondsUntilExpiration/60/60/24))
                          }
                          case expiredControl: PasswordExpiredControl => {
                            appLogTrace("the subject's password has expired, adding the obligation")
                            lc.withObligation(Obligations.CHANGE_PASSWORD.toString)
                          }
                          case _ => {
                            appLogDebug("unknown control, do nothing [name={}, oid={}]", control.getControlName,
                              control.getOID)
                          }
                        }
                      })
                    }
                  })
                  tEntry <- Try({
                    appLogTrace("getting the subject's entry [userDn: {}]", userDn)
                    //todo: add requested attributes from the configuration
                    connection.getEntry(userDn)
                  })
                } yield tEntry

                //analise the result
                tEntry match {
                  case Success(resEntity) => {
                    appLogTrace("got the subject's entry [entry: {}]", resEntity)

                    //todo: stop here
                    Option(resEntity).fold[Result]({
                      appLogError("can't get the subject's entry: check the LDAP access rules. The subject must have " +
                        "an access to his entry.")
                      throw new IllegalAccessException("can't get the subject's entry: check the LDAP access rules. " +
                        "The subject must have an access to his entry.")
                    })(entry => {
                      //todo: add user's attributes to claims of the lc
                      Result.SUCCESS
                    })
                  }
                  case Failure(e) => {
                    e match {
                      case le: LDAPException => {
                        appLogDebug("authentication by LDAP server is failed [userName: {}, error: {}]", lgn, le)

                        errorMapper.get(le.getResultCode).fold({
                          val errorKey = UNMAPPED_ERROR_MSG_PREFIX + le.getResultCode.getName
                          lc.withError(errorKey, Messages(errorKey))
                        })(lc withError _)
                        Result.FAIL
                      }
                      case _ => {
                        appLogError("can't perform authentication be LDAP server. Unknown error has occurred: {}", e)
                        throw e
                      }
                    }
                  }
                }
              }
              case _ => {
                //there aren't login and password in the credentials
                Result.FAIL
              }
            }

          }
        })

        //release the connection
        pool.releaseConnection(connection)
        appLogTrace("authenticate subject by LDAP server is complete [res: {}, login context: {}]", authRes, lc)
        authRes
      }
      case Failure(e) => {
        //can't get connection
        appLogError("can't get a ldap connection from the pool")
        throw e
      }
    }

  }

  //todo: realize it
  override def changePassword(curPswd: String, newPswd: String)
                             (implicit lc: LoginContext, request: Request[AnyContent]): Boolean = {
    throw new UnsupportedOperationException("Hasn't realized yet.")
  }

  private def interpolate(text: String, vars: Map[String, String]) =
    (text /: vars) { (t, kv) => t.replace("${"+kv._1+"}", kv._2)  }

  override def toString: String = {
    val sb =new StringBuilder("LdapLoginModule(")
    sb.append("options -> ").append(options)
    sb.append(")").toString()
  }


  val UNMAPPED_ERROR_MSG_PREFIX = "LdapLoginModule.error."
  val NEAR_PSWD_EXPIRE_WARN_KEY = "LdapLoginModule.warn.nearPswdExpire"
}

private object LdapLoginModule {
  val UNMAPPED_ERROR_MSG_PREFIX = "LdapLoginModule.error."
  val NEAR_PSWD_EXPIRE_WARN_KEY = "LdapLoginModule.warn.nearPswdExpire"

  //todo: add mapper for password expired
  val errorMapper = Map(ResultCode.NO_SUCH_OBJECT -> BuildInError.NO_SUBJECT_FOUND,
                        ResultCode.INVALID_CREDENTIALS -> BuildInError.INVALID_CREDENTIALS,
                        ResultCode.UNWILLING_TO_PERFORM -> BuildInError.ACCOUNT_IS_LOCKED)
}
