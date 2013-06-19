package spray.examples

import akka.actor.Actor
import argonaut._
import argonaut.Argonaut._
import spray.routing.HttpService
import spray.httpx.marshalling.Marshaller
import spray.http._
import spray.http.MediaTypes._

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
trait DemoService extends HttpService with UserModule {

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val demoRoute = {
    get {
      path("") {
        respondWithMediaType(`application/json`) {
          complete(User("Luis", 34, "agorer", Address("Fake Street", 111)))
        }
      }
    }
  }

  implicit val marshaller: Marshaller[User] =
    Marshaller.delegate[User, String](ContentTypes.`application/json`)(_.asJson.nospaces)
}
