package com.blitz.idm.app

/**
 *
 */

class IdmConfiguration(confFile: String) extends MainConfiguration(confFile ,"idm-conf") {

  val idmHome = conf.getString("idm-home")

}
