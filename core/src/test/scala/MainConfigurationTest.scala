/**
 *
 */
import com.blitz.idm.app._
import org.specs2.mutable._

class MainConfigurationTest extends Specification {

  System.setProperty("blitzConfUrl", classOf[MainConfigurationTest].getResource("idm_blitz.conf").toURI.toString)

  "Checking of configuration loader " should {

    "main-conf.data-dir must be set to '/opt/data'" in {
      appConfiguration.main.dataDirPath must be equalTo("./core/target/test-classes")
    }

    "main-conf.logger.dir-of-logs must be set to './core/target/test-classes'" in {
      appConfiguration.main.logger.dirOfLogs must be equalTo("./core/target/test-classes")
    }

  }

}
