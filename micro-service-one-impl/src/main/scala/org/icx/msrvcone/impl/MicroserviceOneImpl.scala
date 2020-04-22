package org.icx.msrvcone.impl

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.ServiceCall
import org.icx.msrvcone.api.MicroserviceOneService

import scala.concurrent.{ExecutionContext, Future}

class MicroserviceOneImpl(implicit ec: ExecutionContext) extends MicroserviceOneService {
  override def endPointOne: ServiceCall[NotUsed, String] = ServiceCall{
    _ => Future{"Response from microservice one"}
  }
}
