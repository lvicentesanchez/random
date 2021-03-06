package io.github.lvicentesanchez.marshalling

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.Materializer

import scalaz.{ -\/, \/, \/- }

trait ScalazMarshallers {
  implicit def disjunctionMarshaller[A, B, C](implicit ma: Marshaller[A, C], mb: Marshaller[B, C]): Marshaller[A \/ B, C] =
    Marshaller { implicit ec => _.fold(ma(_), mb(_)) }

  implicit def disjunctionUnmarshaller[A, B, C](implicit mt: Materializer, ua: Unmarshaller[A, B], ub: Unmarshaller[A, C]): Unmarshaller[A, B \/ C] =
    Unmarshaller[A, B \/ C](implicit ec => value =>
      ua(value).map(-\/(_)).recoverWith {
        case _ => ub(value).map(\/-(_))
      })
}

object ScalazMarshallers extends ScalazMarshallers