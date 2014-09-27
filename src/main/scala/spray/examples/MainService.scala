package spray.examples

import spray.http.MediaTypes._
import spray.httpx.marshalling.MetaMarshallers
import spray.routing.{ Directives, HttpService }

trait MainService extends HttpService with ArgonautMarshallers {
  val route = {
    post {
      path("") {
        entity(as[User]) { entity ⇒
          complete(entity.copy(age = entity.age * 2))
        }
      }
    }
  }
}
