package spray.examples

trait AddressModule {
  case class Address(street: String, number: Int)

  object Address {
    import argonaut._
    import Argonaut._

    implicit val codec = casecodec2(Address.apply, Address.unapply)("street", "number")
  }
}
