package spray.examples

case class User(name: String, age: Int, profession: String)

object User {
  import argonaut._
  import Argonaut._

  implicit val codec = casecodec3(User.apply, User.unapply)("name", "age", "profession")
}
