package dummy

import play.api.mvc.{AnyContent, Request}
import com.blitz.idm.app._
import services.login.{BuildInMethods, BasicCredentials, LoginContext, LoginModule}
import LoginModule._

/**
   */
class TestLoginModule1 extends LoginModule {

   def init(options: Map[String, String]): LoginModule = {
     appLogTrace("initializing the test login module 1 [options={}]", options)
     this
   }

   def isYours(implicit lc: LoginContext, request: Request[AnyContent]): Boolean = {
     lc.getCurrentMethod.fold(false)(_ == BuildInMethods.BASIC.id)
   }

   def `do`(implicit lc: LoginContext, request: Request[AnyContent]): Boolean = {
     appLogTrace("doing the basic authentication [login context={}]", lc)

     lc.getCredentials.foldLeft(false)((prevRes, crl) => {
       if (prevRes) {
         prevRes
       } else {
         (crl("lgn").asOpt[String], crl("pswd").asOpt[String]) match {
           case (Some(lgn), Some(pswd)) => {
             if (lgn == "mike" && pswd == "qwerty") {
               appLogDebug("the credentials provided are correct")
               true
             } else {
               appLogDebug("the credentials provided are wrong")
               false
             }
           }
           case _ => {
             false
           }
         }
       }
     })
   }
 }
