package spray.examples

import akka.actor.Actor

class MainServiceActor extends Actor with MainService {
  implicit val actorRefFactory = context

  def receive = runRoute(route)
}
