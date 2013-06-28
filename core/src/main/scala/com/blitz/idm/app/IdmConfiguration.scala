package com.blitz.idm.app

/**
 *
 */

class IdmConfiguration extends MainConfiguration("idm-conf") {

  val idmHome = appConf.getString("idp-home")

}
