package org.icx.msrvcone.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.icx.msrvcone.api.MicroServiceOneService

import scala.concurrent.{ExecutionContext, Future}

class MicroServiceOneImpl(implicit ec: ExecutionContext) extends MicroServiceOneService {
  override def endPointOne: ServiceCall[NotUsed, String] = ServiceCall{
    _ => Future{"Response from micro-service one"}
  }
}
