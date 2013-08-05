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

    "main-conf.logger.levels must be set to 'Map(core -> DEBUG, test -> DEBUG)'" in {
      appConfiguration.main.logger.levels must be equalTo(Map("core" -> "DEBUG", "test" -> "DEBUG"))
    }

    "main-conf.deeply-nested must be set to 'Map(nested-2 -> Map(key4 -> value4, key6 -> value6, key5 -> value5), nested-1 -> Map(key1 -> value1, key3 -> value3, key2 -> value2))'" in {
      appConfiguration.main.getDeepMapString("deeply-nested") must be equalTo(Map("nested-2" -> Map("key4" -> "value4", "key6" -> "value6", "key5" -> "value5"),
        "nested-1" -> Map("key1" -> "value1", "key3" -> "value3", "key2" -> "value2")))
    }

  }

}
