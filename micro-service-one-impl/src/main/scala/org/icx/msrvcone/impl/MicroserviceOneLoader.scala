package org.icx.msrvcone.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import org.icx.msrvcone.api.MicroserviceOneService
import play.api.libs.ws.ahc.AhcWSComponents

class MicroserviceOneLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new MicroserviceOneApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new MicroserviceOneApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[MicroserviceOneService])
}

abstract class MicroserviceOneApplication(context: LagomApplicationContext)
  extends LagomApplication(context) with AhcWSComponents {
  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[MicroserviceOneService](wire[MicroserviceOneImpl])
}


