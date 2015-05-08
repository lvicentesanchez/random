package io.github.lvicentesanchez.marshalling

import akka.http.scaladsl.marshalling.{ Marshaller, PredefinedToResponseMarshallers }
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{ PredefinedFromEntityUnmarshallers, Unmarshaller }
import akka.http.scaladsl.util.{ FastFuture ⇒ FF }
import akka.stream.FlowMaterializer
import argonaut._
import io.github.lvicentesanchez.lambdas.EitherL

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NoStackTrace
import scalaz.~>

trait ArgonautMarshallers extends PredefinedFromEntityUnmarshallers with PredefinedToResponseMarshallers {
  implicit def argonautJsonMarshaller(implicit ec: ExecutionContext): Marshaller[Json, RequestEntity] =
    Marshaller.StringMarshaller.wrap(ContentTypes.`application/json`)(_.nospaces)

  implicit def argonautTMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[T]): Marshaller[T, RequestEntity] =
    argonautJsonMarshaller.compose[T](ev(_))

  implicit def argonautListTMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[List[T]]): Marshaller[List[T], RequestEntity] =
    argonautJsonMarshaller.compose[List[T]](ev(_))

  implicit def argonautJsonUnmarshaller(implicit ec: ExecutionContext, fm: FlowMaterializer): Unmarshaller[HttpEntity, Json] =
    stringUnmarshaller
      .flatMap(str ⇒ disjunctionFutureNT(Parse.parse(str)))

  implicit def argonautTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[T], fm: FlowMaterializer): Unmarshaller[HttpEntity, T] =
    stringUnmarshaller
      .flatMap(str ⇒ disjunctionFutureNT(Parse.decodeEither[T](str)))

  implicit def argonautListTUnmarshaller[T](implicit ec: ExecutionContext, ev: DecodeJson[List[T]], fm: FlowMaterializer): Unmarshaller[HttpEntity, List[T]] =
    stringUnmarshaller
      .flatMap(str ⇒ disjunctionFutureNT(Parse.decodeEither[List[T]](str)))

  private val disjunctionFutureNT: EitherL[String]#T ~> Future = new (EitherL[String]#T ~> Future) {
    def apply[A](fa: EitherL[String]#T[A]): Future[A] =
      fa.fold(
        error ⇒ FF.failed(new Throwable(error) with NoStackTrace),
        FF.successful
      )
  }
}
object ArgonautMarshallers extends ArgonautMarshallers
