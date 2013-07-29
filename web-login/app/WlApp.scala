import com.blitz.idm.app.{MainConfiguration, App}

/**
 *
 */
object WlApp extends App {

  lazy val conf = new WlConfiguration

  def name: String = "Wl"

  def configuration[T >: MainConfiguration]: T = new WlConfiguration

}

class WlConfiguration extends MainConfiguration("wl-conf") {
  implicit val self = this
}