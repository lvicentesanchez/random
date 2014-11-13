package io.github.lvicentesanchez

import akka.http.marshalling.{ Marshaller, PredefinedToResponseMarshallers }
import akka.http.model._
import akka.http.unmarshalling.{ PredefinedFromEntityUnmarshallers, Unmarshaller }
import akka.http.util.{ FastFuture ⇒ FF }
import akka.stream.FlowMaterializer
import argonaut._, Argonaut._
import io.github.lvicentesanchez.lambdas.EitherL
import scala.concurrent.{ ExecutionContext, Future }
import scalaz.~>

trait ArgonautMarshallers extends PredefinedFromEntityUnmarshallers with PredefinedToResponseMarshallers {
  implicit val argonautJsonMarshaller: Marshaller[Json, RequestEntity] =
    Marshaller.opaque { json ⇒ HttpEntity(ContentType(MediaTypes.`application/json`, HttpCharsets.`UTF-8`), json.nospaces) }

  implicit def argonautTMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[T]): Marshaller[T, RequestEntity] =
    argonautJsonMarshaller.compose[T](ev(_))

  implicit def argonautListTMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[List[T]]): Marshaller[List[T], RequestEntity] =
    argonautJsonMarshaller.compose[List[T]](ev(_))

  implicit def argonautJsonUnmarshaller(implicit ec: ExecutionContext, fm: FlowMaterializer): Unmarshaller[HttpEntity, Json] =
    stringUnmarshaller
      .map(Parse.parse)
      .flatMap(disjunctionFutureNT(_))

  implicit def argonautTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[T], fm: FlowMaterializer): Unmarshaller[RequestEntity, T] =
    stringUnmarshaller
      .map(_.decodeEither[T])
      .flatMap(disjunctionFutureNT(_))

  implicit def argonautListTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[List[T]], fm: FlowMaterializer): Unmarshaller[RequestEntity, List[T]] =
    stringUnmarshaller
      .map(_.decodeEither[List[T]])
      .flatMap(disjunctionFutureNT(_))

  private val disjunctionFutureNT: EitherL[String]#T ~> Future = new (EitherL[String]#T ~> Future) {
    def apply[A](fa: EitherL[String]#T[A]): Future[A] =
      fa.fold(
        error ⇒ FF.failed(new Throwable(error)),
        FF.successful
      )
  }
}

object ArgonautMarshallers extends ArgonautMarshallers
