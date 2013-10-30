package spray.examples

import argonaut.{ Json ⇒ _, _ }
import Argonaut._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(name: String, age: Int, profession: String, address: Address)
object User {
  implicit val userCodec: CodecJson[User] = casecodec4(User.apply, User.unapply)("name", "age", "profession", "address")
  implicit val userReader: Reads[User] = Json.reads[User]
  implicit val userWriter: Writes[User] = Json.writes[User]
}