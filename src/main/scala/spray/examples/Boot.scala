package spray.examples

import akka.actor.ActorSystem
import akka.io.IO
import akka.stream.FlowMaterializer
import akka.util.Timeout
import akka.pattern.ask
import akka.http.Http
import akka.http.routing._
import akka.http.model._
import scala.concurrent.duration._

object Boot extends App with ScalaRoutingDSL with ArgonautMarshallers {
  implicit val system = ActorSystem("shall-be-more")
  implicit val materializer = FlowMaterializer()
  implicit val askTimeout: Timeout = 500.millis
  val bindingFuture = (IO(Http) ? Http.Bind(interface = "localhost", port = 8080)).mapTo[Http.ServerBinding]
  val route = {
    post {
      path("") {
          complete(User("Luis", 35, "scalar", Address("johnson", 69)))
      }
    }
  }

  handleConnections(bindingFuture).withRoute(route)
}