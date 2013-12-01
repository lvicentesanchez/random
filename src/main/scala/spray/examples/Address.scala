package spray.examples

import argonaut._
import Argonaut._

case class Address(street: String, number: Int)

object Address {
  implicit val codec: CodecJson[Address] = casecodec2(Address.apply, Address.unapply)("street", "number")
}