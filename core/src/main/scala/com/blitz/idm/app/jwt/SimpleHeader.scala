package com.blitz.idm.app.jwt

import com.blitz.idm.app.json._
import com.blitz.idm.app.security.{KeyStoreManager, TrustedCertsFactory}
import java.security.cert.X509Certificate
import scala.Some

trait SimpleHeaderStore extends HeaderStore{

  def store: JObj

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
    case a: JArr => a.asOpt[Array[String]]
    case JUndef => None
    case _ => throw new IllegalStateException("Header parameter '" + name + "' has wrong type.")
  }

  def getAsStringArray(name: String): Option[Array[String]] = store(name) match {
    case a: JArr => a.asOpt[Array[String]]
    case JUndef => None
    case _ => throw new IllegalStateException("Header parameter '" + name + "' has wrong type.")
  }

  def names: Set[String] = store.fields

  override def toString() = store.toJson

}

/**
 * The simple representation of JWT header.
 */
private[jwt] class SimpleHeader(private val json: String) extends Header with SimpleHeaderStore {

  override def store = JVal.parseStr(json).asInstanceOf[JObj]

}

private[jwt] class SimpleJWSHeader(private val json: String) extends SignHeader with SimpleHeaderStore {

  override def store = JVal.parseStr(json).asInstanceOf[JObj]

}

private[jwt] object SimpleHeaderFactory extends HeaderFactory {

  protected def createPlain(json: String): Header = new SimpleHeader(json)

  protected def createSign(json: String): SignHeader = new SimpleJWSHeader(json)

  protected def createEncryption(json: String): EncryptionHeader = throw new UnsupportedOperationException

}

trait Proxy2KeyStoreManger extends TrustedCertsFactory {

  /**
   * Returns a set of trusted certificates obtained from trusted store.
   * @return a set of trusted certificates
   */
  def trustCerts: Set[X509Certificate] = KeyStoreManager.trustCerts

}
