package com.blitz.idm.app

import scala.io.Source
import scala.annotation.implicitNotFound
import org.slf4j.LoggerFactory

/**
 *
 */
@implicitNotFound(msg = "You do not have an implicit App in scope. If you want to bring the current running App into context, just add import 'com.blitz.idm.app.AppProvider.current'")
trait App {

  def name: String

  def configuration[T >: MainConfiguration]: T

  Logger.doConfigure(AppProvider.classLoader, name, configuration)

  def coreLogger = LoggerFactory.getLogger("core")

  def appLogger = LoggerFactory.getLogger(name)

  def reloadLoggerConfig{
    Logger.doConfigure(AppProvider.classLoader, name, configuration)
  }

}

object AppProvider {

  var classLoader = this.getClass.getClassLoader

  private lazy val _currentApp: App = {
    Option(classLoader.getResource("blitz.app")).fold[App]{
      println("blitz.app not found")
      throw new IllegalStateException("blitz.app not found")
    }(url => {
      val src = Source.fromURI(url.toURI)
      try {
        src.getLines().collectFirst({case l: String if l.startsWith("appClass=") => l.substring(9)})
          .headOption.fold[App]{
          println("app class not defined")
          throw new IllegalStateException("app class not defined")}(clazz => {
          try {
            classLoader.loadClass(clazz + "$").getDeclaredField("MODULE$").get(null).asInstanceOf[App]
          }
          catch {
            case e: ClassNotFoundException => {
              println("singleton '" + clazz + "' not fount")
              throw new IllegalStateException("singleton '" + clazz + "' not fount")
            }
          }
        })
      } finally {if(src != null) src.close()}
    })
  }

  implicit def current = _currentApp

}
