package com.blitz.idm.app

import com.typesafe.config.{Config, ConfigValue, ConfigFactory}
import java.net.URL
import scala.collection.JavaConverters._

/**
 *
 */



class NestedConfiguration(name: String, parentConf: Config) {

  //It is not right, but necessary to glue this configuration to the play framework configuration
  def conf = parentConf.getConfig(name)

  def this(name: String)(nestedConf: NestedConfiguration) = this(name, nestedConf.conf)

  @inline private[this] def _safeUnwrap[B](func:(Config, String) => B)(implicit name2: String): Option[B] = conf.hasPath(name2) match {
    case true => Option(func(conf, name2))
    case false => None
  }

  def getString(name: String) = conf.getString(name)
  def getOptString(implicit name: String) = _safeUnwrap(_.getString(_))

  def getInt(name: String) = conf.getInt(name)
  def getOptInt(implicit name: String) = _safeUnwrap(_.getInt(_))

  def getLong(name: String) = conf.getLong(name)
  def getOptLong(implicit name: String) = _safeUnwrap(_.getLong(_))

  def getBoolean(name: String) = conf.getBoolean(name)
  def getOptBoolean(implicit name: String) = _safeUnwrap(_.getBoolean(_))

  def getConfig(name: String) = conf.getConfig(name)
  def getOptConfig(implicit name: String) = _safeUnwrap(_.getConfig(_))

  @inline private[this] def _toMapString(cnf: Config): Map[String, String] = cnf.entrySet().asScala.map(_.getKey match {case key => key -> cnf.getString(key)}).toMap

  def getMapString(name: String): Map[String, String] = getOptConfig(name).fold[Map[String, String]](Map.empty)(_toMapString _)

  def getDeepMapString(name: String): Map[String, Map[String, String]] = getOptConfig(name).fold[Map[String, Map[String, String]]](Map.empty){c => {
    c.entrySet().asScala.map(_.getKey.split('.')(0)).map{case key => key -> _toMapString(c.getConfig(key))}.toMap
  }}

}

class MainConfiguration(private val appConf: String, private val root: Config = Option(System.getProperty("blitzConfUrl")).fold[Config](
  throw new IllegalStateException("Property 'blitzConfUrl' is undefined.")
)(path => {
  ConfigFactory.parseURL(new URL(path)).resolve()
})) extends NestedConfiguration(appConf, root) {

  val main = new NestedConfiguration("main-conf", root) {

    val dataDirPath = getString("data-dir")

    val logger = new NestedConfiguration("logger")(this) {

      val dirOfLogs = getString("dir-of-logs")

      val levels = getMapString("levels")

    }

    val truststore = new NestedConfiguration("truststore")(this) {

      val path = getString("path")

      val password = getString("password")


    }

    val tests = new NestedConfiguration("tests")(this) {
      val dirOfTests = getOptString("dir-of-tests").getOrElse(dataDirPath)
    }

  }

  private[app] def lookupConfig(key: String) = root.hasPath(key) match {
    case true => Option(root.getString(key))
    case false => None
  }

}
