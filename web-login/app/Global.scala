import play.api.{Configuration, Application, GlobalSettings}
import com.blitz.idm.app._

/**
 *
 */
object Global extends GlobalSettings {

  AppProvider.classLoader = Global.getClass.getClassLoader

  override def onStart(app: Application) {
    super.onStart(app)
    WlApp.reloadLoggerConfig
  }

  override def configuration: Configuration = new Configuration(appConfiguration.conf)

}
