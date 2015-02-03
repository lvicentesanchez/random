package io.github.lvicentesanchez

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.Http
import akka.http.Http.IncomingConnection
import akka.http.model.{ StatusCodes, HttpResponse }
import akka.http.server._
import akka.stream.FlowMaterializer
import akka.stream.scaladsl._
import akka.stream.stage.Stage
import io.github.lvicentesanchez.marshalling.ArgonautMarshallers
import io.github.lvicentesanchez.models.{ User, Request }
import io.github.lvicentesanchez.streams.flexi.DisjunctionRoute
import io.github.lvicentesanchez.streams.source.UnboundedPublisher
import io.github.lvicentesanchez.streams.stage.SafetyStage
import scala.concurrent.ExecutionContext
import scalaz.\/

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

  val mmap: MaterializedMap =
    source.
      map {
        case request @ Request(user @ User(_, age, _, _), _) ⇒
          request.copy(user = user.copy(age = age * 2))
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

  import FlowGraphImplicits._

  FlowGraph { implicit builder ⇒
    val disj: DisjunctionRoute[IncomingConnection, IncomingConnection] = new DisjunctionRoute[IncomingConnection, IncomingConnection]("safe-disjunction")
    binding.connections.transform(() ⇒ SafetyStage(100)) ~> disj.in
    disj.left ~> Sink.foreach[IncomingConnection](_ handleWithSyncHandler (_ ⇒ HttpResponse(StatusCodes.ServiceUnavailable)))
    disj.right ~> Sink.foreach[IncomingConnection](_ handleWith route)
  }.run()
}
