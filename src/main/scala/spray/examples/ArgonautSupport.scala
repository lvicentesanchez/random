package spray.examples

import argonaut._
import Argonaut._
import spray.http.ContentTypes
import spray.httpx.marshalling.Marshaller

trait ArgonautSupport {
  implicit def marshaller[T: CodecJson]: Marshaller[T] =
    Marshaller.delegate[T, String](ContentTypes.`application/json`)(
      implicitly[CodecJson[T]].encode(_).nospaces
    )
}
