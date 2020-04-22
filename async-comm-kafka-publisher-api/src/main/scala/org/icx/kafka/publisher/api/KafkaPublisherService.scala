package org.icx.kafka.publisher.api

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import org.icx.kafka.publisher.api.model.SomeEntity

object KafkaPublisherService {
  val TOPIC_NAME = "learn-lagom"
}

trait KafkaPublisherService extends Service {

  def publishEntityEvent: Topic[SomeEntity]

  override final def descriptor: Descriptor = {
    import Service._
    named("kafka-publisher")
      .withTopics(
        topic(KafkaPublisherService.TOPIC_NAME, publishEntityEvent)
      ).withAutoAcl(true)
  }
}
