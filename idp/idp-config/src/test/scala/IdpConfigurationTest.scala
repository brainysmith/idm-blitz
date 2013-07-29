import com.blitz.idm.idp.config.IdpApp
import org.specs2.mutable._
import com.blitz.idm.app._

/**
 *
 */
class IdpConfigurationTest extends Specification {

  System.setProperty("blitzConfUrl", classOf[MainConfigurationTest].getResource("idm_blitz_idp.conf").toURI.toString)

  appLogDebug("I am running\n")

  "Checking of configuration loader " should {

    "main-conf.data-dir must be set to '/opt/data'" in {
      IdpApp.configuration.main.dataDirPath must be equalTo("./idp-config/target/test-classes")
    }

    "idp-conf.idp-home must be set to '/opt/data/idp'" in {
      IdpApp.configuration.idpHome must be equalTo("./idp-config/target/test-classes/idp")
    }

    "idp-conf.web.context-root must be set to '/idp'" in {
      IdpApp.configuration.web.ctxRoot must be equalTo("/idp")
    }

    "idp-conf.web.https-port must be set to '8080'" in {
      IdpApp.configuration.web.httpsPort must be equalTo(8080)
    }

    "idp-conf.web.weblogin-url must be set to 'https://weblogin.org'" in {
      IdpApp.configuration.web.webloginUrl must be equalTo("https://weblogin.org")
    }

    "idp-conf.cache.session-cache-sec must be set to '3600'" in {
      IdpApp.configuration.cache.sessionCache must be equalTo(3600)
    }

    "idp-conf.cache.login-ctx-cache-sec must be set to '1800'" in {
      IdpApp.configuration.cache.loginCtxCache must be equalTo(1800)
    }

    "idp-conf.cache.logout-ctx-cache-sec must be set to '1800'" in {
      IdpApp.configuration.cache.logoutCtxCache must be equalTo(1800)
    }

    "idp-conf.cache.attribute-cache-sec must be set to '3600'" in {
      IdpApp.configuration.cache.attributeCache must be equalTo(3600)
    }

    "idp-conf.status-page-allowed-ips must be set to '127.0.0.1'" in {
      IdpApp.configuration.statusPageAllowedIps must be equalTo("127.0.0.1")
    }

    "idp-conf.load-balancing-enabled must be set to 'true'" in {
      IdpApp.configuration.loadBalancingEnabled must be equalTo(true)
    }

    "idp-conf.idtoken-lifetime-sec must be set to '3600'" in {
      IdpApp.configuration.idTokenLifetime must be equalTo(3600)
    }

    "idp-conf.entity-id must be set to 'http://main.idp.org'" in {
      IdpApp.configuration.entityId must be equalTo("http://main.idp.org")
    }

  }

}
