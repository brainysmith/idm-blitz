package com.blitz.idm.app.jwt

import org.joda.time.DateTime
import java.util.Date

/**
 * This trait represents IntDate type of JSON Web Token. The type contain the number of seconds from 1970-01-01T0:0:OZ UTC
 * until the specified UTC date/time.
 */

sealed trait IntDate {

  def value: Int

  def before(d: IntDate): Boolean

  def after(d: IntDate): Boolean

}

private class IntDateImpl(private val _value: Int) extends IntDate {

  if (_value < 0) throw new IllegalArgumentException("The number of second from epoch must be non negative.")

  def value = _value

  override def hashCode(): Int = _value.hashCode()

  override def equals(obj: scala.Any): Boolean = _value.equals(obj)

  override def toString: String = _value.toString

  def before(d: IntDate): Boolean = d.value < _value

  def after(d: IntDate): Boolean = d.value > _value
}

object IntDate {

  /**
   * Create an instance of IntDate with the number of seconds from epoch passed.
   * @param seconds - the number os seconds from epoch
   * @return - an instance of type IntDate.
   */
  def apply(seconds: Int): IntDate = new IntDateImpl(seconds)

  /**
   * Creates an instance of IntDate from date/time string in format that is compatible with the ISO 8601 standard.
   * @param strDate - an string representation of date/time
   * @return - an instance of type IntDate
   */
  def apply(strDate: String): IntDate = new IntDateImpl(new DateTime(strDate).getMillis.toInt)

  def now: IntDate = new IntDateImpl((new DateTime().getMillis / 1000).toInt)

}
