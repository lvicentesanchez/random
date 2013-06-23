package spray.examples

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("shall-be-more")

  val service = system.actorOf(Props[MainServiceActor], "main-service")

  IO(Http) ! Http.Bind(service, "0.0.0.0", port = 8080)
}