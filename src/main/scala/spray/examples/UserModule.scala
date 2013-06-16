package spray.examples

trait UserModule extends AddressModule {
  case class User(name: String, age: Int, profession: String, address: Address)

  object User {
    import argonaut._
    import Argonaut._

    implicit val codec = casecodec4(User.apply, User.unapply)("name", "age", "profession", "address")
  }
}
