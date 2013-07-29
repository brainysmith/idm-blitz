package com.blitz.idm.app

import java.util.logging.Level
import org.slf4j.bridge.SLF4JBridgeHandler
import scala.util.control.NonFatal
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import java.io.File
import ch.qos.logback.core.util.StatusPrinter

/**
 *
 */
object Logger {


  def doConfigure(cl: ClassLoader, appName: String, config: MainConfiguration) {

    Option(java.util.logging.Logger.getLogger("")).map{root =>
      root.setLevel(Level.FINEST)
      root.getHandlers.foreach(root.removeHandler _)
    }

    SLF4JBridgeHandler.install()

    try {
      val loggerCtx = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
      loggerCtx.stop()
      val configurator = new JoranConfigurator
      configurator.setContext(loggerCtx)
      loggerCtx.reset()
      loggerCtx.putProperty("dir.logs", config.main.logger.dirOfLogs)

      try {
        Option(cl.getResource("logger.xml")).fold[Unit]{
          println("logger.xml not found")
          throw new IllegalStateException("logger.xml not found")
        }(configurator.doConfigure(_))

        loggerCtx.getLogger(appName).setLevel(ch.qos.logback.classic.Level.INFO)
        config.main.logger.levels.foreach{
          case (logger, level) => loggerCtx.getLogger(logger).setLevel(ch.qos.logback.classic.Level.toLevel(level))
        }
        loggerCtx.start()
      }
      catch {
        case NonFatal(e) => e.printStackTrace()
      }
      StatusPrinter.printIfErrorsOccured(loggerCtx)
    }
    catch {
      case NonFatal(_) =>
    }

  }

}
