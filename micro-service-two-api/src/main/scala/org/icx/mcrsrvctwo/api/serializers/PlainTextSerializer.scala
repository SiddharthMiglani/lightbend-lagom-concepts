package org.icx.mcrsrvctwo.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.NegotiatedSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol

class PlainTextSerializer(val charset: String) extends NegotiatedSerializer[String, ByteString] {
  override val protocol = MessageProtocol(Some("text/plain"), Some(charset))

  def serialize(s: String) = ByteString.fromString(s, charset)
}
