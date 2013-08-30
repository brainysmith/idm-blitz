package com.blitz.idm.app.json

import scala.annotation.implicitNotFound

/**
 *
 */
@implicitNotFound("No JSON writer found for type ${T}. Try to implement an implicit JWriter.")
trait JWriter[T] {

  def write(o: T): JVal

}

object JWriter extends DefaultJWriters {
}

trait DefaultJWriters {

  implicit object IntJWriter extends JWriter[Int] {
    def write(o: Int): JVal = JNum(o)
  }

  implicit object StringJWriter extends JWriter[String] {
    def write(o: String): JVal = JStr(o)
  }

  implicit object BooleanJWriter extends JWriter[Boolean] {
    def write(o: Boolean): JVal = JBool(o)
  }

  implicit def arrayJWriter[T](implicit writer: JWriter[T]): JWriter[Array[T]] = new JWriter[Array[T]] {
    def write(o: Array[T]): JVal = JArr(o.map(t => Json.toJson(t)(writer)))
  }

  implicit object JStrWriter extends JWriter[JStr] {
    def write(o: JStr): JVal = o
  }

  implicit object JNUmWriter extends JWriter[JNum] {
    def write(o: JNum): JVal = o
  }

  implicit object JBoolWriter extends JWriter[JBool] {
    def write(o: JBool): JVal = o
  }

  implicit object JArrWriter extends JWriter[JArr] {
    def write(o: JArr): JVal = o
  }

  implicit object JObjWriter extends JWriter[JObj] {
    def write(o: JObj): JVal = o
  }

}
