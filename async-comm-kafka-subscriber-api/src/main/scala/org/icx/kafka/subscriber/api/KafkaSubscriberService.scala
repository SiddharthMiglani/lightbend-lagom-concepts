package org.icx.kafka.subscriber.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import org.icx.kafka.publisher.api.model.Item

trait KafkaSubscriberService extends Service {

  def findLatestItem: ServiceCall[NotUsed, Item]

  override final def descriptor: Descriptor = {
    import Service._
    named("kafka-subscriber")
      .withCalls(
        restCall(Method.GET, "/kafkaSubscriber/findLatestItem", findLatestItem)
      ).withAutoAcl(true)
  }
}
