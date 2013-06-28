/**
 *
 */

import com.blitz.idm.app.{IdmApp}
import org.specs2.mutable._

class MainConfigurationTest extends Specification {

  "Checking of configuration loader " should {

    "main-conf.data-dir must be set to '/opt/data'" in {
      IdmApp.conf.dataDirPath must be equalTo("/opt/data")
    }

    "idm-conf.idp-home must be set to '/opt/data/idp'" in {
      IdmApp.conf.idmHome must be equalTo("/opt/data/idp")
    }

  }

}
