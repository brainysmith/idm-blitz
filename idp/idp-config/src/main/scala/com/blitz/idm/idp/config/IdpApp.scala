package com.blitz.idm.idp.config

import com.blitz.idm.app.{NestedConfiguration, MainConfiguration}

/**
  * The the configuration singleton of IdP application.
  * Example:
  *
  * main-conf {
  *   data-dir = "/opt/data"
  * }
  *
  * idp-conf {
  *   idp-home = ${main-conf.data-dir}"/idp"
 *
 *    web {
 *      context-root = "/idp"
 *      https-port = 8080
 *      weblogin-url = "https://weblogin.org"
 *    }
 *
 *    cache {
 *      session-cache-sec = 3600
 *      login-ctx-cache-sec = 1800
 *      logout-ctx-cache-sec = 1800
 *      attribute-cache-sec = 3600
 *    }
 *
 *    status-page-allowed-ips = "127.0.0.1"
 *    load-balancing-enabled = true
 *    idtoken-lifetime-sec = 3600
 *    entity-id = "http://main.idp.org"
 *
 * }
 */

class IdpConfiguration extends MainConfiguration("idp-conf") {
  implicit val self = this

  val idpHome = conf.getString("idp-home")

  val web = new NestedConfiguration("web") {

    val ctxRoot = conf.getString("context-root")
    val httpsPort = conf.getInt("https-port")
    val webloginUrl = conf.getString("weblogin-url")

  }

  val cache = new NestedConfiguration("cache") {

    val sessionCache = conf.getLong("session-cache-sec")
    val loginCtxCache = conf.getLong("login-ctx-cache-sec")
    val logoutCtxCache = conf.getLong("logout-ctx-cache-sec")
    val attributeCache = conf.getLong("attribute-cache-sec")

  }

  val statusPageAllowedIps = conf.getString("status-page-allowed-ips")
  val loadBalancingEnabled = conf.getBoolean("load-balancing-enabled")
  val idTokenLifetime = conf.getLong("idtoken-lifetime-sec")
  val entityId = conf.getString("entity-id")

}

object IdpApp {

  lazy val conf = new IdpConfiguration

  lazy val javaProxyConf = IdpConfigurationJavaProxy

}

object IdpConfigurationJavaProxy {

  val idpHome = IdpApp.conf.idpHome

  val web_ctxRoot = IdpApp.conf.web.ctxRoot
  val web_httpsPort = IdpApp.conf.web.httpsPort
  val web_webloginUrl = IdpApp.conf.web.webloginUrl

  val cache_sessionCache = IdpApp.conf.cache.sessionCache
  val cache_loginCtxCache = IdpApp.conf.cache.loginCtxCache
  val cache_logoutCtxCache = IdpApp.conf.cache.logoutCtxCache
  val cache_attributeCache = IdpApp.conf.cache.attributeCache

  val statusPageAllowedIps = IdpApp.conf.statusPageAllowedIps
  val loadBalancingEnabled = IdpApp.conf.loadBalancingEnabled
  val idTokenLifetime = IdpApp.conf.idTokenLifetime
  val entityId = IdpApp.conf.entityId

}