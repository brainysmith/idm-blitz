package conf

import com.blitz.idm.app.{MainConfiguration, App}

/**
 * Example:
 * wl-conf {
 *    loginModules {
 *      com.blitz.idm.login.LdapLoginModule = {
 *        order = 1
 *        param1 = value 1
 *        param2 = value 2
 *        ...
 *      }
 *
 *      com.blitz.idm.login.SmartCardLoginModule = {
 *        ...
 *      }
 *
 *      ...
 *    }
 *    loginFlow = services.login.DefaultLoginFlow
 * }
 */
class WlConfiguration extends MainConfiguration("wl-conf") {
  implicit val self = this

  val loginModules = getDeepMapString("loginModules")
  val loginFlow = getOptString("loginFlow")
}

object WlApp extends App {

  lazy val conf = new WlConfiguration

  def name: String = "Wl"

  def configuration[T >: MainConfiguration]: T = new WlConfiguration

}