import java.io.File
import play.api.{Mode, Configuration, Application, GlobalSettings}

/**
 *
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) {
    super.onStart(app)
    println("Im getting started.")
  }

  override def configuration: Configuration = new Configuration(WebloginApp.conf.conf)

}
