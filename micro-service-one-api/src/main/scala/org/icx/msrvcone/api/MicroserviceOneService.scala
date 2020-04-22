package org.icx.msrvcone.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait MicroserviceOneService extends Service {

  def endPointOne: ServiceCall[NotUsed, String]

  override final def descriptor: Descriptor = {
    import Service._

    named("microserviceone")
      .withCalls(
        restCall(Method.GET, "/microserviceone/endPointOne", endPointOne)
      )
      .withAutoAcl(true)
  }
}
