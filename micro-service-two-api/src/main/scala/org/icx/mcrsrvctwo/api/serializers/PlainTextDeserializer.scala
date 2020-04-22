package org.icx.mcrsrvctwo.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.NegotiatedDeserializer

class PlainTextDeserializer(val charset: String) extends NegotiatedDeserializer[String, ByteString] {
  def deserialize(bytes: ByteString) =
    bytes.decodeString(charset)
}
