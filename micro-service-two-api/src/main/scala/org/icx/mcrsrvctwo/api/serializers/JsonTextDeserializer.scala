package org.icx.mcrsrvctwo.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.NegotiatedDeserializer
import com.lightbend.lagom.scaladsl.api.transport.DeserializationException
import play.api.libs.json.Json

import scala.util.control.NonFatal

class JsonTextDeserializer extends NegotiatedDeserializer[String, ByteString] {
  def deserialize(bytes: ByteString) = {
    try {
      Json.parse(bytes.iterator.asInputStream).as[String]
    } catch {
      case NonFatal(e) => throw DeserializationException(e)
    }
  }
}
