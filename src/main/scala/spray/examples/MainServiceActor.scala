package spray.examples

import akka.actor.{ Actor, PoisonPill }
import spray.can.Http
import spray.http.{ HttpRequest, HttpResponse, Timedout }

class MainServiceActor extends Actor with MainService {
  implicit val actorRefFactory = context

  def receive = connection orElse runRoute(route)

  def connection: Receive = {
    case ev: Http.ConnectionClosed ⇒
      context.stop(self)

    case Timedout(HttpRequest(method, uri, _, _, _)) ⇒
      sender ! HttpResponse(
        status = 500,
        entity = s"The $method request to '$uri' has timed out..."
      )
  }
}
