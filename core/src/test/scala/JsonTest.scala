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
        )).toJson must be equalTo("{\"key4\":[10,\"value2\"],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null,\"key1\":12,\"key2\":\"value2\",\"key3\":true}")
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
        JVal.parseStr("{\"key1\":null,\"key2\":7,\"key3\":12.7,\"key4\":\"some text\",\"key5\":true}").toJson must be equalTo("{\"key4\":\"some text\",\"key5\":true,\"key1\":null,\"key2\":7,\"key3\":12.7}")
      }

      "deserialization of an pure array ([12,\"text\"]) " in {
        JVal.parseStr("[12, \"text\"]").toJson must be equalTo("[12,\"text\"]")
      }

      "deserialization of an really complex object ({\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\",{\"key\":7}],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}) " in {
        JVal.parseStr("{\"key1\":12,\"key2\":\"value2\",\"key3\":true,\"key4\":[10,\"value2\", {\"key\":7}],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null}").toJson must be equalTo("{\"key4\":[10,\"value2\",{\"key\":7}],\"key5\":{\"key6\":17,\"key7\":37},\"key8\":null,\"key1\":12,\"key2\":\"value2\",\"key3\":true}")
      }

      val obj = JObj(Seq(
        "name" -> JStr("John"),
        "lastname" -> JStr("Smith"),
        "address" -> JObj(Seq(
          "city" -> JStr("Moscow")
        ))
      ))

      "extracting 'firstname' from " + obj.toJson + " " in {
        obj \ "firstname" must be equalTo JUndef
      }

      "extracting 'lastname' from " + obj.toJson + " " in {
        obj \ "lastname" must be equalTo JStr("Smith")
      }

      "extracting 'city' from " + obj.toJson + " " in {
        obj \ "address" \ "city" must be equalTo JStr("Moscow")
      }

      "marshalling Int " in {
        Json.toJson(7) must be equalTo JNum(7)
      }

      "marshalling String " in {
        Json.toJson("text") must be equalTo JStr("text")
      }

      "marshalling true " in {
        Json.toJson(true) must be equalTo JBool(true)
      }

      "marshalling " + JArr(Array(JNum(1), JNum(2), JNum(3))) + " " in {
        Json.toJson(Array(1,2,3)).toString must be equalTo JArr(Array(JNum(1), JNum(2), JNum(3))).toString
      }

      "unmarshalling Int " in {
        JNum(7).as[Int] must be equalTo 7
      }

      "unmarshalling String " in {
        JStr("text").as[String] must be equalTo "text"
      }

      "unmarshalling Boolean " in {
        JBool(true).as[Boolean] must be equalTo true
      }

      "unmarshalling " + JArr(Array(JNum(1), JNum(2), JNum(3))) + " " in {
        JArr(Array(JNum(1), JNum(2), JNum(3))).as[Array[Int]].toSeq.toString must be equalTo Array(1, 2, 3).toSeq.toString
      }

      "constructing JObj from name and value " in {
        JObj("key", JStr("value")).toJson must be equalTo "{\"key\":\"value\"}"
      }

      "constructing JObj from a tuple (name, value) " in {
        JObj(("key", JStr("value"))).toJson must be equalTo "{\"key\":\"value\"}"
      }

      val objToTestAdding = JObj(Seq(
        "name" -> JStr("John"),
        "lastname" -> JStr("Smith")
      ))

      "adding an absent filed to JObj " in {
        (objToTestAdding + ("login", JStr("jsmith"))).toJson must be equalTo "{\"name\":\"John\",\"lastname\":\"Smith\",\"login\":\"jsmith\"}"
      }

      "adding an existing filed to JObj " in {
        (objToTestAdding + ("name", JStr("Mike"))).toJson must be equalTo "{\"name\":\"John\",\"lastname\":\"Smith\"}"
      }

      "adding or replace an absent filed to JObj " in {
        (objToTestAdding +! ("login", JStr("jsmith"))).toJson must be equalTo "{\"name\":\"John\",\"lastname\":\"Smith\",\"login\":\"jsmith\"}"
      }

      val objToAdd = JObj(Seq(
        "name" -> JStr("Mike"),
        "login" -> JStr("msmith")
      ))

      "adding or replace an existing filed to JObj " in {
        (objToTestAdding ++! objToAdd).toJson must be equalTo "{\"name\":\"Mike\",\"lastname\":\"Smith\",\"login\":\"msmith\"}"
      }

    }

}
