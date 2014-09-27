package spray.examples

import argonaut._
import Argonaut._
import akka.http.model.{ ContentTypes, ContentTypeRange, HttpCharsets, HttpEntity, MediaTypes }
import akka.http.marshalling.Marshaller
import akka.http.unmarshalling.{ Deserialized, MalformedContent, Unmarshaller }

trait ArgonautMarshallers {
  implicit val utf8StringUnmarshaller = Unmarshaller[HttpEntity, String](entity => entity.toStrict())
    def apply(entity: HttpEntity) = Right(entity.asString(defaultCharset = HttpCharsets.`UTF-8`))
  }

  implicit val argonautJsonMarshaller: Marshaller[Json] =
    Marshaller.delegate[Json, String](ContentTypes.`application/json`)(param ⇒
      s""")]}',
       |${param.nospaces}
      """.stripMargin
    )

  implicit def argonautTMarshaller[T](implicit ev: CodecJson[T]): Marshaller[T] =
    Marshaller.delegate[T, String](ContentTypes.`application/json`)(param ⇒
      s""")]}',
    	 |${ev.encode(param).nospaces}
      """.stripMargin
    )

  implicit def argonautListTMarshaller[T](implicit ev: EncodeJson[List[T]]): Marshaller[List[T]] =
    Marshaller.delegate[List[T], String](ContentTypes.`application/json`)(param ⇒
      s""")]}',
       |${ev.encode(param).nospaces}
      """.stripMargin
    )

  implicit val argonautJsonUnmarshaller: Unmarshaller[Json] =
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
    }
}

object ArgonautMarshallers extends ArgonautMarshallers
