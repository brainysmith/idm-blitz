package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import com.blitz.idm.app._
import play.api.test.FakeApplication
import controllers.routes

/**
 */
class ApplicationSpec extends Specification {

  val FORM_URLENCODED_CONTENT_TYPE =  (CONTENT_TYPE -> "application/x-www-form-urlencoded")

  "Application" should {

    "basic login" in {
      running(FakeApplication()) {
        appLogTrace("Start basic login test")

        val data: Map[String, Seq[String]] = Map("lgn" -> Seq("lgnTest"),
                                                 "pswd" -> Seq("pswdTest"))

        val request = FakeRequest(POST, routes.Login.basicLogin().url)
        val result = route(request.withHeaders(FORM_URLENCODED_CONTENT_TYPE), data).get

        appLogTrace("Get a result: {}", contentAsString(result))
        status(result) must equalTo(OK)
      }
    }
    
/*    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone        
      }
    }
    
    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("Your new application is ready.")
      }
    }*/
  }
}