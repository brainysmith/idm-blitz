package services.login

import play.api.mvc.{AnyContent, Request}
import com.blitz.idm.app._
import Authenticator._

import com.unboundid.util.ssl.{TrustAllTrustManager, SSLUtil}
import com.unboundid.ldap.sdk.{BindResult, LDAPConnectionOptions, LDAPConnectionPool, LDAPConnection}
import scala.util.Try

/**
  */
class LdapBasicAuthenticator extends Authenticator {

  private var options: Map[String, String] = _
  private var pool: LDAPConnectionPool = _

  //configurable options
  private var host: String = _
  private var port: Int = _
  private var useSSL: Boolean = _
  private var autoReconnect: Boolean = _
  private var initialConnections: Int = _
  private var maxConnections: Int = _
  private var userDnPattern: String = _



  //todo: do it
  @Override
  def init(options: Map[String, String]): Authenticator = {
    appLogTrace("initializing the ldap authenticator [options={}]", options)


    host = options.getOrElse("host", null)
    port = 1636
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
    appLogTrace("doing the basic authentication by ldap [login context={}]", lc)

    //perform login for all credentials
    lc.getCredentials.foldLeft(FAIL)((prevRes, crl) => {
      (prevRes, crl) match {
        case (FAIL, basicCrl: BasicCredentials) => {

          val entry = for {
            connection <- Try({pool.getConnection})
            userDn <- Try({
              interpolate(userDnPattern, Map("USERNAME" -> basicCrl.lgn))
            })
            bindRes <- Try({
              connection.bind(userDn, basicCrl.pswd)
            })
            dummy <- Try({
              //todo check result
              if (!bindRes.getResultCode.isConnectionUsable) {
                throw new RuntimeException("change me")
              }
            })
            entry <- Try({
              //todo: add attributes
              connection.getEntry(userDn)
            })
          } yield entry

          entry



          2
        }
        case _ => prevRes
      }
    })


    //pool.releaseConnection(connection)
    1
  }

  private def interpolate(text: String, vars: Map[String, String]) =
    (text /: vars) { (t, kv) => t.replace("${"+kv._1+"}", kv._2)  }

  override def toString: String = {
    val sb =new StringBuilder("LdapAuthenticator(")
    sb.append("options -> ").append(options)
    sb.append(")").toString()
  }
}
