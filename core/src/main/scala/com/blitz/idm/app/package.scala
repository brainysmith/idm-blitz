package com.blitz.idm

import scala.collection.JavaConverters._

/**
 *
 */
package object app {

  def appName = AppProvider.current.name

  def appConfiguration = AppProvider.current.configuration

  def keystoreManager = AppProvider.current.keystoreManager

  //Logger utils
  def coreLogError(msg: String) {
    AppProvider.current.coreLogger.error(msg)
  }

  def coreLogError(msg: String, args: Any*) {
    AppProvider.current.coreLogger.error(msg, args.toArray.toSeq.asJava.toArray)
  }

  def coreLogWarn(msg: String) {
    AppProvider.current.coreLogger.warn(msg)
  }

  def coreLogWarn(msg: String, args: Any*) {
    AppProvider.current.coreLogger.warn(msg, args.toArray.toSeq.asJava.toArray)
  }

  def coreLogInfo(msg: String) {
    AppProvider.current.coreLogger.info(msg)
  }

  def coreLogInfo(msg: String, args: Any*) {
    AppProvider.current.coreLogger.info(msg, args.toArray.toSeq.asJava.toArray)
  }

  def coreLogDebug(msg: String) {
    AppProvider.current.coreLogger.debug(msg)
  }

  def coreLogDebug(msg: String, args: Any*) {
    AppProvider.current.coreLogger.debug(msg, args.toArray.toSeq.asJava.toArray)
  }

  def coreLogTrace(msg: String) {
    AppProvider.current.coreLogger.trace(msg)
  }

  def coreLogTrace(msg: String, args: Any*) {
    AppProvider.current.coreLogger.trace(msg, args.toArray.toSeq.asJava.toArray)
  }

  def appLogError(msg: String) {
    AppProvider.current.appLogger.error(msg)
  }

  def appLogError(msg: String, args: Any*) {
    AppProvider.current.appLogger.error(msg, args.toArray.toSeq.asJava.toArray)
  }

  def appLogWarn(msg: String) {
    AppProvider.current.appLogger.warn(msg)
  }

  def appLogWarn(msg: String, args: Any*) {
    AppProvider.current.appLogger.warn(msg, args.toArray.toSeq.asJava.toArray)
  }

  def appLogInfo(msg: String) {
    AppProvider.current.appLogger.info(msg)
  }

  def appLogInfo(msg: String, args: Any*) {
    AppProvider.current.appLogger.info(msg, args.toArray.toSeq.asJava.toArray)
  }

  def appLogDebug(msg: String) {
    AppProvider.current.appLogger.debug(msg)
  }

  def appLogDebug(msg: String, args: Any*) {
    AppProvider.current.appLogger.debug(msg, args.toArray.toSeq.asJava.toArray)
  }

  def appLogTrace(msg: String) {
    AppProvider.current.appLogger.trace(msg)
  }

  def appLogTrace(msg: String, args: Any*) {
    AppProvider.current.appLogger.trace(msg, args.toArray.toSeq.asJava.toArray)
  }

}
