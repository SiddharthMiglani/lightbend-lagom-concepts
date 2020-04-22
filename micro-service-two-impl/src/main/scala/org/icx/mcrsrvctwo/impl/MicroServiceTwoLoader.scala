package org.icx.mcrsrvctwo.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import org.icx.mcrsrvctwo.api.MicroServiceTwoService
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

/**
  * Having created our application cake (check and understand abstract class MicroserviceTwoApplication below),
  * we can now write an application loader.
  *
  * Play’s mechanism for loading an application is for the application to provide an application loader. Play will
  * pass some context information to this loader, such as a classloader,the running mode, and any extra configuration,
  * so that the application can bootstrap itself.
  *
  * Lagom provides a convenient mechanism for implementing above, the LagomApplicationLoader
  *
  * The loader has two methods:
  *   load (must be implemented)
  *   loadDevMode
  */
class MicroServiceTwoLoader extends LagomApplicationLoader {

  /**
    * Here we’ve mixed in LagomDevModeComponents that provides the dev mode service locator and
    * registers the services with it in dev mode
    *
    * @param context
    * @return a LagomApplication object
    */
  override def loadDevMode(context: LagomApplicationContext) =
    new MicroServiceTwoApplication(context) with LagomDevModeComponents

  /**
    * For prod mode, for now, we’ve simply provided NoServiceLocator as the service locator - this is a service
    * locator that will return nothing for every lookup. We’ll see in the deploying to production documentation
    * how to select the right service locator for production.
    *
    * @param context
    * @return a LagomApplication object
    */
  override def load(context: LagomApplicationContext): LagomApplication =
    new MicroServiceTwoApplication(context) {
      override def serviceLocator: ServiceLocator = ServiceLocator.NoServiceLocator
    }

  /**
    * A third method, describeService, is optional, but may be used by tooling to discover what service APIs are
    * offered by this service. The metadata read from here may in turn be used to configure service gateways and
    * other components.
    *
    * Note: Fact that service is accessible via service gateway and service gateway is able to list down the paths of
    * all the available endpoints is due to the use of .withAutoAcl(true) in service descriptor (MicroServiceTwoService)
    * So, still not very sure of what below method does.
    * @return
    */
  override def describeService = Some(readDescriptor[MicroServiceTwoService])

}

/**
  * The simplest way to build the Application cake and then wire your code inside it is by creating an
  * abstract class that extends LagomApplication
  *
  * LagomApplication that this class extends require three things:
  *
  * wcClient: WSClient (provided by mixin AhcWSComponents)
  * lagomServer: LagomServer (defined below in abstract class)
  * serviceLocator: ServiceLocator (will be defined in the concrete implementation of this class)
  *
  * The reason that this class is defined abstract is because:
  * typical application will use different service locators in different environments,
  * in development, it will use the service locator provided by the Lagom development environment,
  * while in production it will use whatever is appropriate for your production environment,
  * such as the service locator implementation provided by Akka Discovery Service Locator.
  *
  * So our main application cake leaves this method abstract so that it can mix in the right one
  * depending on which mode it is in when the application gets loaded.
  *
  * @param context
  */
abstract class MicroServiceTwoApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Lagom will use this to discover your service bindings and create a Play router for handling your service calls.
  // You can see that we’ve bound one service descriptor, the MicroserviceTwoService, to our MicroserviceTwoImpl implementation.
  // The name of the Service Descriptor (remember named("service name").withCalls()) you bind will be used as the
  // Service name, that is used in cross-service communication to identify the client making a request.
  override lazy val lagomServer = serverFor[MicroServiceTwoService](wire[MicroServiceTwoImpl])
}
