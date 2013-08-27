import com.blitz.idm.app.json._
import com.blitz.idm.app.json.JArr
import com.blitz.idm.app.json.JObj
import org.specs2.mutable.Specification

/**
 *
 */
class JsonTest extends Specification {

  {
  }

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

      "deserialization" in {

        val value = JVal.parseStr("{\"key\":{\"key2\":7}}")

        println("Processed: " + value.toJson + "\n")

        JObj(Seq("key1" -> JNum(12),
          "key2" -> JStr("value2"),
          "key3" -> JBool(true),
          "key4" -> JArr(Array(JNum(10), JStr("value2"))),
          "key5" -> JObj(Seq("key6" -> JNum(17),
            "key7" -> JNum(37))),
          "key8" -> JNull
        )).toJson must be equalTo("{\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\"],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}")
      }

    }

}
