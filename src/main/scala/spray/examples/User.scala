package spray.examples

import argonaut._
import Argonaut._

case class User(name: String, age: Int, profession: String, address: Address)

object User {
  implicit val codec: CodecJson[User] = casecodec4(User.apply, User.unapply)("name", "age", "profession", "address")
}