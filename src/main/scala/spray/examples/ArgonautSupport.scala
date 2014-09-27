package spray.examples

import argonaut._
import akka.http.model._
import akka.http.marshalling.{ Marshaller, PredefinedToResponseMarshallers }

import scala.concurrent.ExecutionContext

trait ArgonautMarshallers extends PredefinedToResponseMarshallers {
  implicit val argonautJsonMarshaller: Marshaller[Json, RequestEntity] =
    Marshaller.withOpenCharset(MediaTypes.`application/json`) { (s, cs) ⇒ HttpEntity(ContentType(MediaTypes.`application/json`, cs), s.nospaces) }

  implicit def argonautTEntityMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[T]): Marshaller[T, RequestEntity] =
    argonautJsonMarshaller.compose[T](ev(_))

  implicit def argonautListTEntityMarshaller[T](implicit ec: ExecutionContext, ev: EncodeJson[List[T]]): Marshaller[List[T], RequestEntity] =
    argonautJsonMarshaller.compose[List[T]](ev(_))

  /*implicit val argonautJsonUnmarshaller: Unmarshaller[Json] =
    delegate[String, Json](MediaTypes.`application/json`)(string ⇒
      JsonParser.parse(string).toEither.left.map(error ⇒ MalformedContent(error))
    )

  implicit def argonautTUnmarshaller[T](implicit ev: DecodeJson[T]): Unmarshaller[T] =
    delegate[String, T](MediaTypes.`application/json`)(string ⇒
      string.decodeEither[T].toEither.left.map(error ⇒ MalformedContent(error))
    )

  implicit def argonautListTUnmarshaller[T](implicit ev: DecodeJson[List[T]]): Unmarshaller[List[T]] =
    delegate[String, List[T]](MediaTypes.`application/json`)(string ⇒
      string.decodeEither[List[T]].toEither.left.map(error ⇒ MalformedContent(error))
    )

  private def delegate[A, B](unmarshalFrom: ContentTypeRange*)(f: A ⇒ Deserialized[B])(implicit ma: Unmarshaller[A]): Unmarshaller[B] =
    new SimpleUnmarshaller[B] {
      val canUnmarshalFrom = unmarshalFrom
      def unmarshal(entity: HttpEntity) = ma(entity).right.flatMap(a ⇒ f(a))
    }*/
}

object ArgonautMarshallers extends ArgonautMarshallers