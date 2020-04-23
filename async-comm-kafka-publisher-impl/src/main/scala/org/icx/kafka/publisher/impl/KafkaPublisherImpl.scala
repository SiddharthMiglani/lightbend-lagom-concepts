package org.icx.kafka.publisher.impl

import akka.Done
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import org.icx.kafka.publisher.api.KafkaPublisherService
import org.icx.kafka.publisher.api.model.Item

class KafkaPublisherImpl(registry: PersistentEntityRegistry) extends KafkaPublisherService {

  override def addItem: ServiceCall[Item, Done] = ServiceCall { item =>
    val ref = registry.refFor[ItemEntity](item.id.toString)
    ref.ask(PutItem(item))
  }

  override def publishItemEvents: Topic[Item] = TopicProducer.singleStreamWithOffset {
    offset =>
      registry.eventStream(ItemEventTag.INSTANCE, offset)
        .map(event => (convertEvent(event), offset))
  }

  private def convertEvent(entityEvent: EventStreamElement[ItemEvent]): Item = {
    entityEvent.event match {
      case ItemSavedEvent(item) => item
    }
  }
}
