package org.icx.kafka.subscriber.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import org.icx.kafka.publisher.api.model.Item

object ItemSerializerRegistry extends JsonSerializerRegistry{
  override def serializers: Seq[JsonSerializer[_]] = Vector(JsonSerializer[Item])
}
