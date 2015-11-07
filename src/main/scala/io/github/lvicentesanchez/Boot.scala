package io.github.lvicentesanchez

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{ Source, Sink, Flow }
import akka.stream.{ ActorMaterializer, Materializer }
import io.github.lvicentesanchez.marshalling.ArgonautMarshallers
import io.github.lvicentesanchez.models.User
import scala.concurrent.{ ExecutionContext, Future }
import scala.io.StdIn

object Boot extends App with Directives with ArgonautMarshallers {
  implicit val system: ActorSystem = ActorSystem("random")
  implicit val asynchronous: ExecutionContext = system.dispatcher
  implicit val materialiser: Materializer = ActorMaterializer()
  val transform: Flow[User, User, Unit] =
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
            Source.single(user).via(transform).runWith[Future[User]](Sink.head)
          )
        )
      }
    }

  val binding: Future[Http.ServerBinding] = Http().bindAndHandle(route, interface = "0.0.0.0", port = 9000)

  StdIn.readLine()

  binding.flatMap(_.unbind()).onComplete { _ ⇒
    system.shutdown()
    system.awaitTermination()
  }
}
