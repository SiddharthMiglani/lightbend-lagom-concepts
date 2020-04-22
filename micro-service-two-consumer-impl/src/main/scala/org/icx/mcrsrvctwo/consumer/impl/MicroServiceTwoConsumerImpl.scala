package org.icx.mcrsrvctwo.consumer.impl

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.icx.mcrsrvctwo.api.MicroServiceTwoService
import org.icx.mcrsrvctwo.consumer.api.MicroServiceTwoConsumerService

import scala.concurrent.ExecutionContext

/**
  * An implementation of consumed service is created in the application loader (check the abstract class in loader).
  *
  * That implementation or client can be used anywhere in the Lagom application. Typically this will be done by
  * passing the client to another component, such as this i.e. a service implementation, via that components
  * constructor, which will be done automatically for you if you’re using Macwire
  *
  * @param microServiceTwo - a microservice two client created in the application loader
  * @param ec
  */
class MicroServiceTwoConsumerImpl(microServiceTwo: MicroServiceTwoService)(implicit ec: ExecutionContext)
  extends MicroServiceTwoConsumerService {

  override def ping: ServiceCall[NotUsed, String] = ServiceCall{ _ =>
    microServiceTwo.ping.invoke()
      .map { response => s"Microservice two says: $response" }
  }

  override def streamingEndPoint: ServiceCall[NotUsed, Source[String, NotUsed]] = { _ =>

    /**
      * calls the streaming end point of microservice two with a delay of two seconds and a custom message
      */
    microServiceTwo
      .streamingEndPoint(2000)
      .invoke("I've been invoked from inside consumer of microservice two")
  }
}
