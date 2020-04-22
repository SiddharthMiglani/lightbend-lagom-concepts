package org.icx.mcrsrvctwo.consumer.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait MicroServiceTwoConsumerService extends Service {

  def ping: ServiceCall[NotUsed, String]
  def streamingEndPoint: ServiceCall[NotUsed, Source[String, NotUsed]]

  override def descriptor: Descriptor = {
    import Service._

    named("micro-service-two-consumer")
      .withCalls(
        pathCall("/microservicetwo/consume/ping", ping),
        pathCall("/microservicetwo/consume/streamingEndPoint", streamingEndPoint)
      )
      .withAutoAcl(true)
  }
}
