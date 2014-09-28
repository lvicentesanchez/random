package io.github.lvicentesanchez

import akka.http.marshalling.{ Marshaller, PredefinedToResponseMarshallers }
import akka.http.model._
import akka.http.unmarshalling.{ PredefinedFromEntityUnmarshallers, Unmarshaller }
import akka.http.util.{ FastFuture ⇒ FF }
import akka.stream.FlowMaterializer
import argonaut._, Argonaut._
import scala.concurrent.ExecutionContext
import scalaz.{ -\/, \/- }

trait ArgonautMarshallers extends PredefinedFromEntityUnmarshallers with PredefinedToResponseMarshallers {
  implicit val argonautJsonMarshaller: Marshaller[Json, RequestEntity] =
    Marshaller.opaque { json ⇒ HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), json.nospaces) }

  implicit def argonautTEntityMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[T]): Marshaller[T, RequestEntity] =
    argonautJsonMarshaller.compose[T](ev(_))

  implicit def argonautListTEntityMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[List[T]]): Marshaller[List[T], RequestEntity] =
    argonautJsonMarshaller.compose[List[T]](ev(_))

  implicit def argonautJsonUnmarshaller(implicit ec: ExecutionContext, fm: FlowMaterializer): Unmarshaller[HttpEntity, Json] =
    stringUnmarshaller.map(Parse.parse).flatMap {
      case -\/(string) ⇒ FF.failed(new Throwable(string))
      case \/-(value) ⇒ FF.successful(value)
    }

  implicit def argonautTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[T], fm: FlowMaterializer): Unmarshaller[HttpEntity, T] =
    stringUnmarshaller.map(_.decodeEither[T]).flatMap {
      case -\/(string) ⇒ FF.failed(new Throwable(string))
      case \/-(value) ⇒ FF.successful(value)
    }

  implicit def argonautListTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[List[T]], fm: FlowMaterializer): Unmarshaller[HttpEntity, List[T]] =
    stringUnmarshaller.map(_.decodeEither[List[T]]).flatMap {
      case -\/(string) ⇒ FF.failed(new Throwable(string))
      case \/-(value) ⇒ FF.successful(value)
    }
}

object ArgonautMarshallers extends ArgonautMarshallers