package spray.examples

import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("shall-be-more")

  val handler = system.actorOf(Props[Handler])

  IO(Http) ! Http.Bind(handler, "0.0.0.0", port = 8080)
}