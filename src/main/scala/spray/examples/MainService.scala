package spray.examples

import spray.http.MediaTypes._
import spray.routing.HttpService

trait MainService extends HttpService with UserModule with ArgonautSupport {
  val route = {
    get {
      path("") {
        respondWithMediaType(`application/json`) {
          complete(User("Luis", 34, "agorer", Address("Fake Street", 111)))
        }
      }
    }
  }
}
