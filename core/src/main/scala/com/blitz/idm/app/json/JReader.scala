package com.blitz.idm.app.json

import scala.annotation.implicitNotFound
import scala.reflect.ClassTag

/**
 *
 */
@implicitNotFound("No JSON reader found for type ${T}. Try to implement an implicit JReader.")
trait JReader[T] {

  def read(v: JVal): JResult[T]

}

object JReader extends DefaultJReaders

trait DefaultJReaders {

  implicit object IntJReader extends JReader[Int] {
    def read(v: JVal): JResult[Int] = v match {
      case JNum(i) => JSuccess(i.toInt)
      case _ => JError("json.error.expected.number")
    }
  }

  implicit object StringJReader extends JReader[String] {
    def read(v: JVal): JResult[String] = v match {
      case JStr(s) => JSuccess(s)
      case _ => JError("json.error.expected.string")
    }
  }

  implicit object BooleanJReader extends JReader[Boolean] {
    def read(v: JVal): JResult[Boolean] = v match {
      case JBool(b) => JSuccess(b)
      case _ => JError("json.error.expected.boolean")
    }
  }

  implicit def arrayJReader[T : ClassTag](implicit reader: JReader[T]): JReader[Array[T]] = new JReader[Array[T]] {
    def read(v: JVal): JResult[Array[T]] = v match {
      case JArr(a) => JSuccess(a.map(e => e.as[T](reader)).toArray[T])
      case _ => JError("json.error.expected.array")
    }
  }

}
