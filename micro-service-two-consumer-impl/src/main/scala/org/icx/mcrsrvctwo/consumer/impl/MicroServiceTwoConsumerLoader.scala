package org.icx.mcrsrvctwo.consumer.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import com.softwaremill.macwire.wire
import org.icx.mcrsrvctwo.api.MicroServiceTwoService
import org.icx.mcrsrvctwo.consumer.api.MicroServiceTwoConsumerService
import play.api.libs.ws.ahc.AhcWSComponents

class MicroServiceTwoConsumerLoader extends LagomApplicationLoader {
  override def loadDevMode(context: LagomApplicationContext) =
    new MicroServiceTwoConsumerApplication(context) with LagomDevModeComponents

  override def load(context: LagomApplicationContext): LagomApplication =
    new MicroServiceTwoConsumerApplication(context) {
      override def serviceLocator: ServiceLocator = ServiceLocator.NoServiceLocator
    }

  override def describeService = Some(readDescriptor[MicroServiceTwoConsumerService])
}

abstract class MicroServiceTwoConsumerApplication(context: LagomApplicationContext) extends
  LagomApplication(context)
  with AhcWSComponents {

  override lazy val lagomServer = serverFor[MicroServiceTwoConsumerService](wire[MicroServiceTwoConsumerImpl])

  /**
    * Lagom provides a macro called implement on the ServiceClient class to create implementation of another
    * service. The ServiceClient is provided by the LagomServiceClientComponents, which is already implemented
    * by LagomApplication.
    */
  lazy val microServiceTwo = serviceClient.implement[MicroServiceTwoService]
}
