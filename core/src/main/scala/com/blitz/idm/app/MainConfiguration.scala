package com.blitz.idm.app

import com.typesafe.config.{Config, ConfigValue, ConfigFactory}
import java.net.URL
import scala.collection.JavaConverters._
import org.specs2.internal.scalaz.Functor

/**
 *
 */



class NestedConfiguration(name: String, parentConf: Config) {

  def conf = parentConf.getConfig(name)

  def this(name: String)(nestedConf: NestedConfiguration) = this(name, nestedConf.conf)

}


class MainConfiguration(private val appConf: String, private val root: Config = Option(System.getProperty("blitzConfUrl")).fold[Config](
  throw new IllegalStateException("Property 'blitzConfUrl' is undefined.")
)(path => {
  ConfigFactory.parseURL(new URL(path)).resolve()
})) extends NestedConfiguration(appConf, root) {

  val main = new NestedConfiguration("main-conf", root) {

    val dataDirPath = conf.getString("data-dir")

    val logger = new NestedConfiguration("logger")(this) {

      val dirOfLogs = conf.getString("dir-of-logs")
      val levels = conf.hasPath("levels") match {
        case true => {a:Config => {a.entrySet().asScala.map{_.getKey match {
          case key @ "root" => "ROOT" -> a.getString(key)
          case key => key -> a.getString(key)
        }}.toMap
        }}.apply(conf.getConfig("levels"))
        case false => Map.empty[String, String]
      }

    }

  }

  private[app] def lookupConfig(key: String) = root.hasPath(key) match {
    case true => Option(root.getString(key))
    case false => None
  }

}
