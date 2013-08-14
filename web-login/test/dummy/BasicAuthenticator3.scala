package dummy

import play.api.mvc.{AnyContent, Request}
import com.blitz.idm.app._
import services.login.{BasicCredentials, LoginContext, Authenticator}
import services.login.Authenticator._

/**
   */
class BasicAuthenticator3 extends Authenticator {

   def init(options: Map[String, String]): Authenticator = {
     appLogTrace("initializing the basic authenticator 3 [options={}]", options)
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
             appLogDebug("the credentials provided are correct")
             SUCCESS
           } else {
             appLogDebug("the credentials provided are wrong")
             FAIL
           }
         }
         case _ => prevRes
       }
     })
   }
 }
