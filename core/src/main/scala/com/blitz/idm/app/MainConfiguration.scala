package com.blitz.idm.app

import com.typesafe.config.ConfigFactory

/**
 *
 */

class MainConfiguration(appConfName: String) {

  val mainConfName = "main-conf"

  private val config = ConfigFactory.load("idm_blitz")

  private val mainConf = config.getConfig(mainConfName)

  val appConf = config.getConfig(appConfName)



  val dataDirPath = mainConf.getString("data-dir")

}
