package spray.examples

import argonaut.{ Json ⇒ _, _ }
import Argonaut._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Address(street: String, number: Int)
object Address {
  implicit val addressCodec: CodecJson[Address] = casecodec2(Address.apply, Address.unapply)("street", "number")
  implicit val addressReader: Reads[Address] = Json.reads[Address]
  implicit val addressWriter: Writes[Address] = Json.writes[Address]
}