package io.github.lvicentesanchez

import akka.http.marshalling.Marshaller
import akka.http.unmarshalling.Unmarshaller
import scala.concurrent.ExecutionContext
import scalaz.{ -\/, \/-, \/ }

trait ScalazMarshallers {
  implicit def disjunctionMarshaller[A, B, C](implicit ma: Marshaller[A, C], mb: Marshaller[B, C]): Marshaller[A \/ B, C] =
    Marshaller { _.fold(ma(_), mb(_)) }

  implicit def disjunctionUnmarshaller[A, B, C](implicit ec: ExecutionContext, ua: Unmarshaller[A, B], ub: Unmarshaller[A, C]): Unmarshaller[A, B \/ C] =
    Unmarshaller[A, B \/ C](value ⇒
      ua(value).map(-\/(_)).recoverWith {
        case _ ⇒ ub(value).map(\/-(_))
      }
    )
}

object ScalazMarshallers extends ScalazMarshallers