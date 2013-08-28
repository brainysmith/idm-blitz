import com.blitz.idm.app.json._
import com.blitz.idm.app.json.JArr
import com.blitz.idm.app.json.JObj
import org.specs2.mutable.Specification
import scala.collection.mutable.ListBuffer

/**
 *
 */
class JsonTest extends Specification {

    "Checking of JSON serialization" should {

      "serialization result must be equal to '{\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\"],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}'" in {
        JObj(Seq("key1" -> JNum(12),
          "key2" -> JStr("value2"),
          "key3" -> JBool(true),
          "key4" -> JArr(Array(JNum(10), JStr("value2"))),
          "key5" -> JObj(Seq("key6" -> JNum(17),
            "key7" -> JNum(37))),
          "key8" -> JNull
        )).toJson must be equalTo("{\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\"],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}")
      }

      "deserialization of nested objects ({\"key\":{\"key2\":7}}) " in {
        JVal.parseStr("{\"key\":{\"key2\":7}}").toJson must be equalTo("{\"key\":{\"key2\":7}}")
      }

      "deserialization of pure number (12) " in {
        JVal.parseStr("12").toJson must be equalTo("12")
      }

      "deserialization of pure float number (12.7) " in {
        JVal.parseStr("12.7").toJson must be equalTo("12.7")
      }

      "deserialization of pure string (\"text\") " in {
        JVal.parseStr("\"text\"").toJson must be equalTo("\"text\"")
      }

      "deserialization of pure boolean (false) " in {
        JVal.parseStr("false").toJson must be equalTo("false")
      }

      "deserialization of pure boolean (true) " in {
        JVal.parseStr("true").toJson must be equalTo("true")
      }

      "deserialization of an object with null field ({\"key\":null}) " in {
        JVal.parseStr("{\"key\":null}").toJson must be equalTo("{\"key\":null}")
      }

      "deserialization of an complex object  ({\"key1\":null,\"key2\":7,\"key3\":12.7,\"key4\":\"some text\",\"key5\":true}) " in {
        JVal.parseStr("{\"key1\":null,\"key2\":7,\"key3\":12.7,\"key4\":\"some text\",\"key5\":true}").toJson must be equalTo("{\"key1\":null,\"key2\":7,\"key3\":12.7,\"key4\":\"some text\",\"key5\":true}")
      }

      "deserialization of an pure array ([12,\"text\"]) " in {
        JVal.parseStr("[12, \"text\"]").toJson must be equalTo("[12,\"text\"]")
      }

      "deserialization of an really complex object ({\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\"],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}) " in {
        JVal.parseStr("{\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\"],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}").toJson must be equalTo("{\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\"],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}")
      }

    }

}
