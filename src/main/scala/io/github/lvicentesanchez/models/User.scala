package io.github.lvicentesanchez.models

import argonaut._, Argonaut._

case class User(name: String, age: Int)

object User {
  implicit val codec: CodecJson[User] = casecodec2(User.apply, User.unapply)("name", "age")
}
