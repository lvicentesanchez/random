package io.github.lvicentesanchez

import argonaut.Argonaut._
import argonaut._

case class Address(street: String, number: Int)

object Address {
  implicit val codec: CodecJson[Address] = casecodec2(Address.apply, Address.unapply)("street", "number")
}