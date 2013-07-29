/**
 *
 */
import com.blitz.idm.app._
import org.specs2.mutable._

class MainConfigurationTest extends Specification {

  System.setProperty("blitzConfUrl", classOf[MainConfigurationTest].getResource("idm_blitz.conf").toURI.toString)

  appLogDebug("I have been logged!\n")

  "Checking of configuration loader " should {

    "main-conf.data-dir must be set to '/opt/data'" in {
      appConfiguration.main.dataDirPath must be equalTo("./core/target/test-classes")
    }

    /*"main-conf.logger.conf-file must be set to '/opt/data/log'" in {
      appConfiguration.main.logger.confFile must be equalTo("/opt/data")
    }*/

  }

}
