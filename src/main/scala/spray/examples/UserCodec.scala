package spray.examples

import argonaut._
import Argonaut._

trait UserCodec extends UserDef with AddressCodec {
  implicit val userCodec: CodecJson[User] = casecodec4(User.apply, User.unapply)("name", "age", "profession", "address")
}
