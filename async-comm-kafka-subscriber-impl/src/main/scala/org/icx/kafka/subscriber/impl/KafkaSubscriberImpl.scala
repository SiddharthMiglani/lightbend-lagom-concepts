package org.icx.kafka.subscriber.impl

import akka.{Done, NotUsed}
import akka.stream.scaladsl.Flow
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.icx.kafka.publisher.api.KafkaPublisherService
import org.icx.kafka.publisher.api.model.Item
import org.icx.kafka.subscriber.api.KafkaSubscriberService

import scala.concurrent.{ExecutionContext, Future}

class KafkaSubscriberImpl(kafkaPublisherService: KafkaPublisherService) extends KafkaSubscriberService {

  private var message: Item = _

  kafkaPublisherService.publishItemEvents.subscribe.atLeastOnce(Flow[Item].map {
    case item: Item =>
      message = item
      Done
  })

  override def findLatestItem: ServiceCall[NotUsed, Item] = ServiceCall { _ =>
    Future.successful {
      message
    }
  }

}
