package spray.examples

import spray.json._
import DefaultJsonProtocol._

trait AddressProtocol extends AddressDef {
  implicit val addressrProtocol = jsonFormat2(Address)
}