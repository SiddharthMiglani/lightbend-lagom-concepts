package org.icx.kafka.publisher.impl

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import org.icx.kafka.publisher.api.KafkaPublisherService
import org.icx.kafka.publisher.api.model.SomeEntity
import org.icx.kafka.publisher.impl.events.{EntityEvent, EntityEventTag, EntitySavedEvent}

class KafkaPublisherImpl(registry: PersistentEntityRegistry) extends KafkaPublisherService {

  override def publishEntityEvent: Topic[SomeEntity] = TopicProducer.singleStreamWithOffset {
    offset =>
      registry.eventStream(EntityEventTag.INSTANCE, offset)
        .map(event => (convertEvent(event), offset))
  }

  private def convertEvent(entityEvent: EventStreamElement[EntityEvent]): SomeEntity = {
    entityEvent.event match {
      case EntitySavedEvent(someEntity) => someEntity
    }
  }

}
