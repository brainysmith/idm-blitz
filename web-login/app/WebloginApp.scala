import com.blitz.idm.app.MainConfiguration

/**
 *
 */
object WebloginApp {

  lazy val conf = new WlConfiguration

}

class WlConfiguration extends MainConfiguration("wl-conf") {
  implicit val self = this
}