package io.github.lvicentesanchez

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.Http
import akka.http.server._
import akka.stream.FlowMaterializer
import akka.stream.scaladsl._
import io.github.lvicentesanchez.marshalling.ArgonautMarshallers
import io.github.lvicentesanchez.models.{ User, Request }
import io.github.lvicentesanchez.streams.sources.UnboundedPublisher
import scala.concurrent.ExecutionContext

object Boot extends App with Directives with ArgonautMarshallers {
  implicit val system: ActorSystem = ActorSystem("random")
  implicit val asynchronous: ExecutionContext = system.dispatcher
  implicit val materialiser: FlowMaterializer = FlowMaterializer()
  val binding = Http().bind(interface = "0.0.0.0", port = 9000)

  val source: PropsSource[Request] =
    Source(UnboundedPublisher.props[Request])

  val complete: Sink[Request] =
    Sink.foreach {
      case Request(user, fn) ⇒ fn(user)
    }

  val mmap: MaterializedMap = source.
    map {
      case request @ Request(user, _) ⇒ request.copy(user = user.copy(age = user.age * 2))
    }.
    to(complete).
    run()
  val aref: ActorRef = mmap.get(source)

  val route: Route =
    post {
      path("") {
        entity(as[User]) { user ⇒
          completeWith(instanceOf[User])(fn ⇒
            aref ! Request(user, fn)
          )
        }
      }
    }

  binding.startHandlingWith(route)
}
