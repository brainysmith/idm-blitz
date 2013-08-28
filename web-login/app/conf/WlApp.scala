package conf

import com.blitz.idm.app.{MainConfiguration, App}

/**
 * Example:
 * wl-conf {
 *    authenticators {
 *      com.blitz.idm.login.BasicAuthenticator = {
 *        order = 1
 *        param1 = value 1
 *        param2 = value 2
 *        ...
 *      }
 *
 *      com.blitz.idm.login.SmartCardAuthenticator = {
 *        ...
 *      }
 *
 *      ...
 *    }
 * }
 */
class WlConfiguration extends MainConfiguration("wl-conf") {
  implicit val self = this

  val authenticators = getDeepMapString("authenticators")
}

object WlApp extends App {

  lazy val conf = new WlConfiguration

  def name: String = "Wl"

  def configuration[T >: MainConfiguration]: T = new WlConfiguration

}