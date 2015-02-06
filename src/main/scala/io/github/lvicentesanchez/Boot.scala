package io.github.lvicentesanchez

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.server._
import akka.stream.{ ActorFlowMaterializer, FlowMaterializer }
import akka.stream.scaladsl._
import io.github.lvicentesanchez.marshalling.ArgonautMarshallers
import io.github.lvicentesanchez.models.User
import scala.concurrent.ExecutionContext

object Boot extends App with Directives with ArgonautMarshallers {
  implicit val system: ActorSystem = ActorSystem("random")
  implicit val asynchronous: ExecutionContext = system.dispatcher
  implicit val materialiser: FlowMaterializer = ActorFlowMaterializer()
  val binding = Http().bind(interface = "0.0.0.0", port = 9000)
  val transform: Flow[User, User] =
    Flow[User].
      map {
        case user @ User(_, age, _, _) ⇒
          user.copy(age = age * 2)
      }

  val route: Route =
    post {
      path("") {
        entity(as[User])(user ⇒
          complete(
            Source.single(user).via(transform).runWith(Sink.head)
          )
        )
      }
    }

  binding.startHandlingWith(route)
}
