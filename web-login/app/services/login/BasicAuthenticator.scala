package services.login

import play.api.mvc.{AnyContent, Request}
import com.blitz.idm.app._
import Authenticator._

/**
  */
class BasicAuthenticator extends Authenticator {

  private var options: Map[String, String] = _

  def init(options: Map[String, String]): Authenticator = {
    appLogTrace("initializing the basic authenticator [options={}]", options)
    this.options = options
    this
  }

  def isYours(implicit lc: LoginContext, request: Request[AnyContent]): Boolean = {
    lc.getMethod == BASIC_METHOD
  }

  def `do`(implicit lc: LoginContext, request: Request[AnyContent]): Int = {
    appLogTrace("doing the basic authentication [login context={}]", lc)

    lc.getCredentials.foldLeft(FAIL)((prevRes, crl) => {
      (prevRes, crl) match {
        case (FAIL, basicCrl: BasicCredentials) => {
          //todo: change implementation
          if (basicCrl.lgn == "mike" && basicCrl.pswd == "qwerty") {
            appLogDebug("the credentials provided are correct [credentials = {}]", basicCrl)
            SUCCESS
          } else {
            appLogDebug("the credentials provided are wrong [credentials = {}]", basicCrl)
            FAIL
          }
        }
        case _ => prevRes
      }
    })
  }

  override def toString: String = {
    val sb =new StringBuilder("BasicAuthenticator(")
    sb.append("options -> ").append(options)
    sb.append(")").toString()
  }
}
