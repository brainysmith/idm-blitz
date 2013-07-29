import com.blitz.idm.app.{MainConfiguration, App}

/**
 *
 */
object DefaultApp extends App {

  def name: String = "test"

  def configuration[T >: MainConfiguration]: T = new MainConfiguration("idm-conf")

}
