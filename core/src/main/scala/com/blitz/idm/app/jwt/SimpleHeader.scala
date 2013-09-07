package com.blitz.idm.app.jwt

import com.blitz.idm.app.json._
import scala.Some

/**
 * The simple representation of JWT header.
 */
private[jwt] class SimpleHeader(private val json: String) extends Header with HeaderStore {

  val store = JVal.parseStr(json).asInstanceOf[JObj]

  def getAsString(name: String): Option[String] = store(name) match {
    case JStr(s) => Some(s)
    case JUndef => None
    case _ => throw new IllegalStateException("Header parameter '" + name + "' has wrong type, expected String.")
  }

  def getAsInt(name: String): Option[Int] = store(name) match {
    case JNum(s) => Some(s.toInt)
    case JUndef => None
    case _ => throw new IllegalStateException("Header parameter '" + name + "' has wrong type, expected Int.")
  }

  def getAsAny(name: String): Option[Any] = store(name) match {
    case JStr(s) => Some(s)
    case JNum(s) => Some(s.toInt)
    case JBool(s) => Some(s)
    case JArr(a) => Some(a)
    case JUndef => None
    case _ => throw new IllegalStateException("Header parameter '" + name + "' has wrong type, expected Int.")
  }

  def names: Set[String] = store.fields

}

private[jwt] object SimpleHeaderFactory extends HeaderFactory {

  protected def createPlain(json: String): Header = new SimpleHeader(json)

  protected def createSign(json: String): SignHeader = throw new UnsupportedOperationException

  protected def createEncryption(json: String): EncryptionHeader = throw new UnsupportedOperationException

}
