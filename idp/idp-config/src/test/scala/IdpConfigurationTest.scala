import com.blitz.idm.idp.config.IdpApp
import org.specs2.mutable._

/**
 *
 */
class IdpConfigurationTest extends Specification {

  System.setProperty("blitzConfUrl", classOf[MainConfigurationTest].getResource("idm_blitz_idp.conf").toURI.toString)

  "Checking of configuration loader " should {

    "main-conf.data-dir must be set to '/opt/data'" in {
      IdpApp.conf.dataDirPath must be equalTo("/opt/data")
    }

    "idp-conf.idp-home must be set to '/opt/data/idp'" in {
      IdpApp.conf.idpHome must be equalTo("/opt/data/idp")
    }

    "idp-conf.web.context-root must be set to '/idp'" in {
      IdpApp.conf.web.ctxRoot must be equalTo("/idp")
    }

    "idp-conf.web.https-port must be set to '8080'" in {
      IdpApp.conf.web.httpsPort must be equalTo(8080)
    }

    "idp-conf.web.weblogin-url must be set to 'https://weblogin.org'" in {
      IdpApp.conf.web.webloginUrl must be equalTo("https://weblogin.org")
    }

    "idp-conf.cache.session-cache-sec must be set to '3600'" in {
      IdpApp.conf.cache.sessionCache must be equalTo(3600)
    }

    "idp-conf.cache.login-ctx-cache-sec must be set to '1800'" in {
      IdpApp.conf.cache.loginCtxCache must be equalTo(1800)
    }

    "idp-conf.cache.logout-ctx-cache-sec must be set to '1800'" in {
      IdpApp.conf.cache.logoutCtxCache must be equalTo(1800)
    }

    "idp-conf.cache.attribute-cache-sec must be set to '3600'" in {
      IdpApp.conf.cache.attributeCache must be equalTo(3600)
    }

    "idp-conf.status-page-allowed-ips must be set to '127.0.0.1'" in {
      IdpApp.conf.statusPageAllowedIps must be equalTo("127.0.0.1")
    }

    "idp-conf.load-balancing-enabled must be set to 'true'" in {
      IdpApp.conf.loadBalancingEnabled must be equalTo(true)
    }

    "idp-conf.idtoken-lifetime-sec must be set to '3600'" in {
      IdpApp.conf.idTokenLifetime must be equalTo(3600)
    }

    "idp-conf.entity-id must be set to 'http://main.idp.org'" in {
      IdpApp.conf.entityId must be equalTo("http://main.idp.org")
    }

  }

}
