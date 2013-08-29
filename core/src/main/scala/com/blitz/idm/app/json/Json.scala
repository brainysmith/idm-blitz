package com.blitz.idm.app.json

/**
 *
 */
object Json {

  def toJson[T](o: T)(implicit writer: JWriter[T]) = writer.write(o)

  def fromJson[T](v: JVal)(implicit reader: JReader[T]): T = reader.read(v).fold[T](e => throw new IllegalStateException(e.toString()))(v => v)

}
