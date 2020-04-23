package org.icx.kafka.publisher.api

import akka.Done
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import org.icx.kafka.publisher.api.model.Item

object KafkaPublisherService {
  val TOPIC_NAME = "learn-lagom"
}

trait KafkaPublisherService extends Service {

  def addItem: ServiceCall[Item, Done]
  def publishItemEvents: Topic[Item]

  override final def descriptor: Descriptor = {
    import Service._
    named("kafka-publisher")
      .withCalls(
        restCall(Method.POST, "/kafkaPublisher/addItem", addItem _)
      )
      .withTopics(
        topic(KafkaPublisherService.TOPIC_NAME, publishItemEvents)
      ).withAutoAcl(true)
  }
}
