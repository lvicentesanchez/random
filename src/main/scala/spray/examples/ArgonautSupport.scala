package spray.examples

import akka.stream.FlowMaterializer
import argonaut._, Argonaut._
import akka.http.model._
import akka.http.marshalling.{ Marshaller, PredefinedToResponseMarshallers }
import akka.http.unmarshalling.{ Unmarshaller, PredefinedFromEntityUnmarshallers }
import scala.concurrent.{ ExecutionContext, Future }
import scalaz.{ -\/, \/- }

trait ArgonautMarshallers extends PredefinedFromEntityUnmarshallers with PredefinedToResponseMarshallers {
  implicit val argonautJsonMarshaller: Marshaller[Json, RequestEntity] =
    Marshaller.withOpenCharset(MediaTypes.`application/json`) { (s, cs) ⇒ HttpEntity(ContentType(MediaTypes.`application/json`, cs), s.nospaces) }

  implicit def argonautTEntityMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[T]): Marshaller[T, RequestEntity] =
    argonautJsonMarshaller.compose[T](ev(_))

  implicit def argonautListTEntityMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[List[T]]): Marshaller[List[T], RequestEntity] =
    argonautJsonMarshaller.compose[List[T]](ev(_))

  implicit def argonautJsonUnmarshaller(implicit ec: ExecutionContext, fm: FlowMaterializer): Unmarshaller[HttpEntity, Json] =
    stringUnmarshaller.map(Parse.parse).flatMap {
      case -\/(string) ⇒ Future.failed(new Throwable(string))
      case \/-(value) ⇒ Future.successful(value)
    }

  implicit def argonautTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[T], fm: FlowMaterializer): Unmarshaller[HttpEntity, T] =
    stringUnmarshaller.map(_.decodeEither[T]).flatMap {
      case -\/(string) ⇒ Future.failed(new Throwable(string))
      case \/-(value) ⇒ Future.successful(value)
    }

  implicit def argonautListTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[List[T]], fm: FlowMaterializer): Unmarshaller[HttpEntity, List[T]] =
    stringUnmarshaller.map(_.decodeEither[List[T]]).flatMap {
      case -\/(string) ⇒ Future.failed(new Throwable(string))
      case \/-(value) ⇒ Future.successful(value)
    }
}

object ArgonautMarshallers extends ArgonautMarshallers