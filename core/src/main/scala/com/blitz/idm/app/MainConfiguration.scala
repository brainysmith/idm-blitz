package com.blitz.idm.app

import com.typesafe.config.{Config, ConfigFactory}
import java.net.URL
import sun.plugin.dom.exception.InvalidStateException

/**
 *
 */

class NestedConfiguration(private val name: String)(implicit protected val parentConf: NestedConfiguration) {

  protected val conf: Config = if(parentConf == null) {
    Option(System.getProperty("blitzConfUrl")).fold[Config](
      throw new InvalidStateException("Property 'blitzConfUrl' is undefined.")
    )(path => {
      ConfigFactory.parseURL(new URL(path)).resolve()
    })
  }
  else parentConf.conf.getConfig(name)

  def getSiblingConf(name: String) = parentConf.conf.getConfig(name)

}

class MainConfiguration(confFile: String, private val appConf: String) extends NestedConfiguration(appConf)(new NestedConfiguration(confFile)(null)) {

  //Hack for the root configuration
  val mainConf = getSiblingConf("main-conf")

  val dataDirPath = mainConf.getString("data-dir")

}
