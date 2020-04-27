package org.icx.msrvcone.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait MicroServiceOneService extends Service {

  def endPointOne: ServiceCall[NotUsed, String]

  override final def descriptor: Descriptor = {
    import Service._

    named("microserviceone")
      .withCalls(
        restCall(Method.GET, "/microserviceone/ping", endPointOne)
      )
      .withAutoAcl(true)
  }
}
