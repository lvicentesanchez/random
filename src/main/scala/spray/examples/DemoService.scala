package spray.examples

import java.io.File
import org.parboiled.common.FileUtils
import scala.concurrent.duration._
import akka.actor.{ Props, Actor }
import akka.pattern.ask
import spray.routing.{ HttpService, RequestContext }
import spray.routing.directives.CachingDirectives
import spray.can.server.Stats
import spray.can.Http
import spray.httpx.marshalling.Marshaller
import spray.httpx.encoding.Gzip
import spray.util._
import spray.http._
import MediaTypes._
import CachingDirectives._

import argonaut._
import Argonaut._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class DemoServiceActor extends Actor with DemoService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing,
  // timeout handling or alternative handler registration
  def receive = runRoute(demoRoute)
}

// this trait defines our service behavior independently from the service actor
trait DemoService extends HttpService {

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val demoRoute = {
    get {
      path("") {
        respondWithMediaType(`application/json`) {
          complete(User("Pedro", 34, "agorer"))
        }
      }
    }
  }

  implicit val marshaller: Marshaller[User] =
    Marshaller.delegate[User, String](ContentTypes.`application/json`)(_.asJson.nospaces)
}
