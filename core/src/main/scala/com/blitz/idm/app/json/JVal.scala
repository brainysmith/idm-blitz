package com.blitz.idm.app.json

import org.codehaus.jackson.map._
import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.{Version, JsonGenerator}
import org.codehaus.jackson.map.annotate.JsonCachable
import org.codehaus.jackson.map.module.SimpleModule
import org.codehaus.jackson.map.Module.SetupContext
import java.io.StringWriter


/**
 *
 */
trait JVal {
  def toJson: String = JacksonBridge.jvalue2JsonString(this)
}

case object JNull extends JVal

case class JNum(private val value: BigDecimal) extends JVal

object JNum {
  implicit def bigDecimalConverter(j: JNum): BigDecimal = j.value
}

case class JStr(private val value: String) extends JVal

object JStr {
  implicit def stringConverter(j: JStr): String = j.value
}

case class JBool(private val value: Boolean) extends JVal

object JBool {
  implicit def booleanConverter(j: JBool): Boolean = j.value
}

case class JArr(v: Array[JVal]) extends JVal {
  val value = v.clone()
  def apply[B <: JVal](idx: Int): B = value(idx).asInstanceOf[B]
}

case class JObj(v: Seq[(String, JVal)]) extends JVal{
  val value = v.toMap
  def apply[B <: JVal](name: String): B = value(name).asInstanceOf[B]
}

@JsonCachable
private[json] class JSerializer extends JsonSerializer[JVal]{
  def serialize(bean: JVal, generator: JsonGenerator, provider: SerializerProvider) {
    bean match {
      case JNull => generator.writeNull()
      case JNum(v) => generator.writeNumber(v.bigDecimal)
      case JStr(v) => generator.writeString(v)
      case JBool(v) => generator.writeBoolean(v)
      case JArr(v) => {
        generator.writeStartArray()
        v.foreach(serialize(_, generator, provider))
        generator.writeEndArray()
      }
      case JObj(v) => {
        generator.writeStartObject()
        v.foreach(e => {
          generator.writeFieldName(e._1)
          serialize(e._2, generator, provider)
        })
        generator.writeEndObject()
      }
    }
  }
}

private[json] class JSerializers extends Serializers.Base {
  override def findSerializer(config: SerializationConfig, javaType: JavaType, beanDesc: BeanDescription, property: BeanProperty) =
    if(classOf[JVal].isAssignableFrom(beanDesc.getBeanClass)) {
      new JSerializer
    }
    else {
      null
    }.asInstanceOf[JsonSerializer[Object]]
}

private[json] object JacksonBridge {

  val mapper = new ObjectMapper

  object module extends SimpleModule("BlitzModule", new Version(1, 0, 0, null)) {
    override def setupModule(context: SetupContext) {
      context.addSerializers(new JSerializers)
    }
  }
  mapper.registerModule(module)

  private[this] val factory = mapper.getJsonFactory

  private[this] def generator(output: StringWriter) = factory.createJsonGenerator(output)

  private[json] def jvalue2JsonString(value: JVal): String = {
    val writer = new StringWriter
    mapper.writeValue(generator(writer), value)
    writer.flush()
    writer.toString
  }

}




