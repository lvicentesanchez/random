package io.github.lvicentesanchez

import akka.actor.{ ActorSystem, Terminated }
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.stream.{ ActorMaterializer, Materializer }
import io.github.lvicentesanchez.api.TravelAPI
import io.github.lvicentesanchez.modules.TravelModule

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }

object Boot extends App {

  import RouteConcatenation._

  implicit val system: ActorSystem = ActorSystem("random")
  implicit val asynchronous: ExecutionContext = system.dispatcher
  implicit val materialiser: Materializer = ActorMaterializer()

  val travelAPI: TravelAPI = TravelAPI(asynchronous, materialiser, TravelModule)

  val route: Route =
    travelAPI.api.reduceOption(_ ~ _).getOrElse(Directives.reject)

  val binding: Future[Http.ServerBinding] =
    Http().bindAndHandle(route, interface = "0.0.0.0", port = 9000)

  System.out.println("Started!")

  Runtime.getRuntime.addShutdownHook(new Thread {
    override def run(): Unit = {
      val terminated: Future[Terminated] =
        for {
          server ← binding
          _ ← server.unbind()
          result ← system.terminate()
        } yield result

      Await.result(terminated, Duration.Inf)
    }
  })
}
