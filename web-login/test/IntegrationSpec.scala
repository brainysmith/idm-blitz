package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers.routes

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends Specification {
  
  "WEB-LOGIN" should {
    
    "base login" in {
      running(TestServer(3333, FakeApplication()), HTMLUNIT) { browser =>

        val call = routes.Login.getPage()
        browser.goTo("http://localhost:3333" + call.url)
        browser.title() must ==/("Login")
        /*browser.takeScreenShot()*/

        browser.fill("#lgn").`with`("mike")
        browser.fill("#pswd").`with`("password")
        browser.click("#loginFm button")

        /*System.out.println(browser.pageSource())*/

        browser.pageSource must contain("Login")
      }
    }
    
  }
  
}