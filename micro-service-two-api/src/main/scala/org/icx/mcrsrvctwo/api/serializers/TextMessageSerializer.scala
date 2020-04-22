package org.icx.mcrsrvctwo.api.serializers

import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.{MessageSerializer, StrictMessageSerializer}
import com.lightbend.lagom.scaladsl.api.transport.{MessageProtocol, NotAcceptable, UnsupportedMediaType}

/**
  *  Lagom provides two sub interfaces of MessageSerializer, StrictMessageSerializer and StreamedMessageSerializer,
  *  which differ primarily in the wire format that they serialize and deserialize to and from.
  *
  *  Strict message serializers serialize and deserialize to and from ByteString, that is, they work strictly in memory,
  *  while streamed message serializers work with streams, that is, Source[ByteString, _].
  *
  *  This class is
  */
class TextMessageSerializer extends StrictMessageSerializer[String] {

  override def acceptResponseProtocols = List(
    MessageProtocol(Some("text/plain")),
    MessageProtocol(Some("application/json"))
  )

  override def serializerForRequest: MessageSerializer.NegotiatedSerializer[String, ByteString] = new PlainTextSerializer("utf-8")

  override def serializerForResponse(acceptedMessageProtocols: Seq[MessageProtocol]): MessageSerializer.NegotiatedSerializer[String, ByteString] = {
    acceptedMessageProtocols match {
      case Nil => new PlainTextSerializer("utf-8")
      case protocols =>
        protocols
          .collectFirst {
            case MessageProtocol(Some("text/plain" | "text/*" | "*/*" | "*"), charset, _) =>
              new PlainTextSerializer(charset.getOrElse("utf-8"))
            case MessageProtocol(Some("application/json"), _, _) =>
              new JsonTextSerializer
          }
          .getOrElse {
            throw NotAcceptable(acceptedMessageProtocols, MessageProtocol(Some("text/plain")))
          }
    }
  }

  override def deserializer(protocol: MessageProtocol): MessageSerializer.NegotiatedDeserializer[String, ByteString] = {
    protocol.contentType match {
      case Some("text/plain") | None =>
        new PlainTextDeserializer(protocol.charset.getOrElse("utf-8"))
      case Some("application/json") =>
        new JsonTextDeserializer
      case _ =>
        throw UnsupportedMediaType(protocol, MessageProtocol(Some("text/plain")))
    }
  }


}
