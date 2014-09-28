package io.github.lvicentesanchez

import akka.http.marshalling.Marshaller
import akka.http.unmarshalling.Unmarshaller
import akka.http.util.{ FastFuture ⇒ FF }
import scala.concurrent.ExecutionContext
import scalaz.{ -\/, \/-, \/ }

trait ScalazMarshallers {
  implicit def scalazEitherMarshaller[A, B, C](implicit ma: Marshaller[A, C], mb: Marshaller[B, C]): Marshaller[A \/ B, C] =
    Marshaller {
      case -\/(error) ⇒ ma(error)
      case \/-(value) ⇒ mb(value)
    }

  implicit def scalazEitherMarshaller[A, B, C](implicit ec: ExecutionContext, ua: Unmarshaller[A, B], ub: Unmarshaller[A, C]): Unmarshaller[A, B \/ C] =
    Unmarshaller[A, B \/ A](value ⇒
      ua(value).map(\/.left[B, A]).recover {
        case _ ⇒ \/.right[B, A](value)
      }
    ).flatMap {
      case -\/(error) ⇒ FF.successful(\/.left[B, C](error))
      case \/-(value) ⇒ ub(value).map(\/.right[B, C])
    }
}

object ScalazMarshallers extends ScalazMarshallers