package play.api.json

import org.specs2.mutable._
import play.api.json._

object JsonSpec extends Specification {

  case class User(id: Long, name: String, friends: List[User])

  implicit object UserFormat extends Format[User] {
    def reads(json: JsValue): User = User(
      (json \ "id").as[Long],
      (json \ "name").as[String],
      (json \ "friends").asOpt[List[User]].getOrElse(List()))
    def writes(u: User): JsValue = JsObject(Map(
      "id" -> JsNumber(u.id),
      "name" -> JsString(u.name),
      "friends" -> JsArray(u.friends.map(fr => JsObject(Map("id" -> JsNumber(fr.id), "name" -> JsString(fr.name)))))))
  }

  case class Car(id: Long, models: Map[String, String])

  implicit object CarFormat extends Format[Car] {
    def reads(json: JsValue): Car = Car(
      (json \ "id").as[Long], (json \ "models").as[String, String])
    def writes(c: Car): JsValue = JsObject(Map(
      "id" -> JsNumber(c.id),
      "models" -> JsObject(c.models.map(x => x._1 -> JsString(x._2)))))
  }

  "JSON" should {
    "handle maps" in {
      val c = Car(1, Map("ford" -> "1954 model"))
      val jsonCar = toJson(c)
      jsonCar.as[Car] must equalTo(c)
    }
    "serialize and deserialize" in {
      val luigi = User(1, "Luigi", List())
      val kinopio = User(2, "Kinopio", List())
      val yoshi = User(3, "Yoshi", List())
      val mario = User(0, "Mario", List(luigi, kinopio, yoshi))
      val jsonMario = toJson(mario)
      jsonMario.as[User] must equalTo(mario)
      (jsonMario \\ "name") must equalTo(Seq(JsString("Mario"), JsString("Luigi"), JsString("Kinopio"), JsString("Yoshi")))
    }

  }
}
