package io.github.lvicentesanchez.api

import akka.http.scaladsl.server.{ Directives, Route }
import akka.stream.Materializer
import io.github.lvicentesanchez.marshalling.ArgonautMarshallers
import io.github.lvicentesanchez.models.User
import io.github.lvicentesanchez.modules.TravelModule

import scala.concurrent.ExecutionContext

/**
 * Created by luissanchez on 05/02/2016.
 */
trait TravelAPI {

  def api: List[Route]

  def timeTravel: Route
}

object TravelAPI {
  def apply(context: ExecutionContext, materialiser: Materializer, travel: TravelModule): TravelAPI =
    new TravelAPI {

      import ArgonautMarshallers._
      import Directives._

      implicit val e = context
      implicit val m = materialiser

      override val timeTravel: Route =
        post {
          path("") {
            entity(as[User]) { user => complete(travel.double(user)) }
          }
        }

      override val api: List[Route] = List(timeTravel)
    }
}
