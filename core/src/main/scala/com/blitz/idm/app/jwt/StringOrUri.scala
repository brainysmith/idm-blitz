package com.blitz.idm.app.jwt

import java.net.URI

/**
 *
 */
sealed trait StringOrUri {

  def isUri: Boolean

  def uri: URI

  def optUri = isUri match {
    case true => Some(uri)
    case false => None
  }

  override def hashCode(): Int = content.hashCode()

  override def equals(obj: scala.Any): Boolean = content.equals(obj)

  override def toString = content.toString

  protected def content: {def hashCode(): Int; def equals(obj: scala.Any): Boolean; def toString: String}

}

object StringOrUri {

  def apply(str: String) = if(str.indexOf(':') == -1) new StringVersion(str) else new UriVersion(str)

}

private final class StringVersion(private val str: String) extends StringOrUri {

  def isUri = false

  def uri = throw new NoSuchElementException

  protected def content: AnyRef =  str
}

private final class UriVersion(private val str: String) extends StringOrUri {

  private val parsed = new URI(str)

  def uri = parsed

  def isUri = true

  protected def content: AnyRef =  uri
}
