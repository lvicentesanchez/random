package spray.examples

import akka.http.unmarshalling.Unmarshal
import argonaut._, Argonaut._
import akka.actor.ActorSystem
import akka.http.Http
import akka.http.server._
import akka.io.IO
import akka.stream.FlowMaterializer
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Boot extends App with ScalaRoutingDSL with ArgonautMarshallers {
  implicit val system: ActorSystem = ActorSystem("shall-be-more")
  implicit val context: ExecutionContext = system.dispatcher
  implicit val materializer: FlowMaterializer = FlowMaterializer()
  implicit val askTimeout: Timeout = 500.millis
  val bindingFuture = (IO(Http) ? Http.Bind(interface = "localhost", port = 8080)).mapTo[Http.ServerBinding]
  val route = {
    post {
      path("") { ctxt ⇒
        ctxt.complete(
          Unmarshal(ctxt.request.entity).to[User].map(
            user ⇒ user.copy(age = user.age * 2)
          )
        )
      }
    }
  }

  handleConnections(bindingFuture).withRoute(route)
}