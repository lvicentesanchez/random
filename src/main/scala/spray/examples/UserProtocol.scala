package spray.examples

import spray.json._
import DefaultJsonProtocol._

trait UserProtocol extends UserDef with AddressProtocol {
  implicit val userProtocol = jsonFormat4(User)
}