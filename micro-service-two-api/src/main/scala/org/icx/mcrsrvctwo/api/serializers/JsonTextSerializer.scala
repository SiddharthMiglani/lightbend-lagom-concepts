package org.icx.mcrsrvctwo.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer.NegotiatedSerializer
import com.lightbend.lagom.scaladsl.api.transport.MessageProtocol
import play.api.libs.json.{JsString, Json}

class JsonTextSerializer extends NegotiatedSerializer[String, ByteString] {
  override val protocol = MessageProtocol(Some("application/json"))

  def serialize(s: String) =
    ByteString.fromString(Json.stringify(JsString(s)))
}