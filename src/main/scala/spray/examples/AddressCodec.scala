package spray.examples

import argonaut._
import Argonaut._

trait AddressCodec extends AddressDef {
  implicit val addressCodec: CodecJson[Address] = casecodec2(Address.apply, Address.unapply)("street", "number")
}
