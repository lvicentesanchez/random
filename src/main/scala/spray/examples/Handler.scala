package spray.examples

import akka.actor.{ Actor, PoisonPill, Props }
import scala.util.Random
import spray.can.Http

class Handler extends Actor {
  def receive: Receive = {
    case _: Http.Connected ⇒
      val service = context.system.actorOf(Props[MainServiceActor], s"main-service-${Random.nextInt()}")
      sender ! Http.Register(service)

    case m @ _ ⇒
  }
}
