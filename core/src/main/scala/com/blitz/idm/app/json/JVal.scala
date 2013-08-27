package com.blitz.idm.app.json

import org.codehaus.jackson.map._
import org.codehaus.jackson.`type`.JavaType
import org.codehaus.jackson.{JsonToken, JsonParser, Version, JsonGenerator}
import org.codehaus.jackson.map.annotate.JsonCachable
import org.codehaus.jackson.map.module.SimpleModule
import org.codehaus.jackson.map.Module.SetupContext
import java.io.StringWriter
import org.codehaus.jackson.map.`type`.TypeFactory
import scala.collection.mutable


/**
 *
 */
trait JVal {
  def toJson: String = JacksonBridge.jVal2JsonString(this)
}

object JVal {
  def parseStr(str: String): JVal = JacksonBridge.jsonString2JVal(str)
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

class JObjStart
object JObjStart extends JObjStart
class JObjEnd
object JObjEnd extends JObjEnd
case class JObjField(name: String) //what about value

class JArrStart
object JArrStart
class JArrEnd
object JArrEnd

trait JVisitor {
  def visit(e: JObjStart)
  def visit(e: JObjEnd)
  def visit(e: JObjField)

  def visit(e: JArrStart)
  def visit(e: JArrEnd)

  def visit(e: JNum)
  def visit(e: JStr)
  def visit(e: JBool)

  def produce: JVal
}

class JValVisitor extends JVisitor {

  //0 - Undefined, 1 - object, 2 - array, 3 - field
  var level = 0
  val levels = mutable.Stack[(Int, AnyRef)]()
  var cur: AnyRef = null
  var finalElem: JVal = _

  private def popLevel() {
    val old = cur
    levels.pop() match {
      case (l, c) => level = l; cur = c
    }
    old
  }

  def visit(e: JObjStart) {
    if(level != 0) {
      levels.push((level, cur))
    }
    cur = mutable.Seq[(String, JVal)]()
    level = 1
  }

  def visit(e: JObjEnd) {
    if(level != 1) {
      throw new IllegalStateException("Malformed JSON. Called JObjEnd but current level is " + level)
    }
    val obj = JObj(cur.asInstanceOf[mutable.Seq[(String, JVal)]])
    if(levels.isEmpty) {
      finalElem = obj
    }
    else {
      popLevel()
      level match {
        case 2 => cur.asInstanceOf[mutable.Seq[JVal]] :+ obj
        case 3 =>
        case (_ @ l, _) => throw new IllegalStateException("Malformed JSON. After JObjEnd on stack found level = " + l)
      }
    }

  }

  def visit(e: JObjField) {
    if(level != 0) {
      levels.push((level, cur))
    }
    cur = e.name
    level = 3
  }

  def visit(e: JArrStart) {}

  def visit(e: JArrEnd) {}

  def visit(e: JNum) {
    level match {
      case 2 => cur.asInstanceOf[Seq[JVal]] :+ e
      case 3 => {level = 1
        cur = levels.pop() match {
          case (1, s) => s.asInstanceOf[mutable.Seq[(String, JVal)]] :+ (cur, e)
          case (_ @ l, _) => throw new IllegalStateException("Malformed JSON. After finishing filed on stack found level = " + l)
        }
      }
    }
  }

  private def fieldEnd(v: JVal) {
    val fieldName = popLevel()
    if(level != 1)
      throw new IllegalStateException("Mailformed JSON. After finishing a filed found level = " + level + " on the stack")
    cur.asInstanceOf[mutable.Seq[(String, JVal)]] :+ (fieldName, v)
  }

  def visit(e: JStr) {}

  def visit(e: JBool) {}

  def produce: JVal = finalElem
}

@JsonCachable
private[json] class JDeserializer(factory: TypeFactory, klass: Class[_]) extends JsonDeserializer[Object] {
  def deserialize(jp: JsonParser, ctx: DeserializationContext): JVal = {

    val visitor: JVisitor = new JValVisitor

    if(!jp.hasCurrentToken)
      jp.nextToken()
    var tkn = jp.getCurrentToken

    while(tkn != null) {
      tkn match {
        case JsonToken.NOT_AVAILABLE => throw new IllegalStateException("A JSON stream has finished unexpectedly")
        case JsonToken.VALUE_EMBEDDED_OBJECT => throw new IllegalStateException("An embedded object found.")
        case _ => accept(tkn, jp, visitor)
      }
      tkn = jp.nextToken()
    }

    val value = visitor.produce
    if (!klass.isAssignableFrom(value.getClass)) {
      throw ctx.mappingException(klass)
    }
    value
  }

  def accept(tkn: JsonToken, jp: JsonParser, visitor: JVisitor) {
    tkn match {
      case JsonToken.START_OBJECT => visitor.visit(JObjStart)
      case JsonToken.FIELD_NAME => visitor.visit(new JObjField(jp.getCurrentName))
      case JsonToken.END_OBJECT => visitor.visit(JObjEnd)
      case JsonToken.VALUE_NUMBER_INT => visitor.visit(JNum(jp.getIntValue))
      case _ => throw new IllegalStateException("Found unknown token " + tkn)
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

private[json] class JDeserializers extends Deserializers.Base {
  override def findBeanDeserializer(javaType: JavaType, config: DeserializationConfig,
                                    provider: DeserializerProvider, beanDesc: BeanDescription,
                                    property: BeanProperty) = {
    val klass = javaType.getRawClass
    if (classOf[JVal].isAssignableFrom(klass)) {
      new JDeserializer(config.getTypeFactory, klass)
    } else null
  }

}

private[json] object JacksonBridge {

  val mapper = new ObjectMapper

  object module extends SimpleModule("BlitzModule", new Version(1, 0, 0, null)) {
    override def setupModule(context: SetupContext) {
      context.addSerializers(new JSerializers)
      context.addDeserializers(new JDeserializers)
    }
  }
  mapper.registerModule(module)

  private[this] val factory = mapper.getJsonFactory

  private[this] def generator(output: StringWriter) = factory.createJsonGenerator(output)

  private[this] def jsonParser(s: String) = factory.createJsonParser(s)

  private[json] def jVal2JsonString(value: JVal): String = {
    val writer = new StringWriter
    mapper.writeValue(generator(writer), value)
    writer.flush()
    writer.toString
  }

  private[json] def jsonString2JVal(strJson: String): JVal = {
    mapper.readValue(jsonParser(strJson), classOf[JVal])
  }

}




