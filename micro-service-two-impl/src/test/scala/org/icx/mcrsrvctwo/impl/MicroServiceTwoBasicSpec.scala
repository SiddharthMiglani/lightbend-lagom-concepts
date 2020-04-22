package org.icx.mcrsrvctwo.impl


import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.icx.mcrsrvctwo.api.MicroServiceTwoService
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpec

/**
  * The test is using ScalaTest’s asynchronous test support.
  *
  * The actual test itself returns a future (check signature of in method), and ScalaTest ensures that
  * that future is handled appropriately.
  */
class MicroServiceTwoBasicSpec extends AsyncWordSpec with Matchers {

  /**
    * withServer takes three parameters:
    *   a setup parameter:
    *     used to configure how the environment should be setup, for example, it can be used to start Cassandra.
    *
    *   a constructor for a LagomApplication:
    *     for which, we mix in LocalServiceLocator (remember the LagomDevModeComponents in actual application loader)
    *     It will resolve just the services that our application is running itself, and is how the service client we
    *     construct knows where to find our running service.
    *
    *   a block to run the actual test:
    *     takes the started server as an input
    *     callback, we implement a service client, which we can then use to talk to our service.
    */
  "Microservice two" should {
    "respond from endPointOne" in
      ServiceTest.withServer(ServiceTest.defaultSetup)
      { ctx => new MicroServiceTwoApplication(ctx) with LocalServiceLocator }
      { server =>

        /**
          * Lagom provides a macro called implement on the ServiceClient class to create implementation of another
          * service. Creating a client in real application and in test case has no major difference except that:
          *
          * in actual application, the ServiceClient is provided by the LagomServiceClientComponents, which is already
          * implemented by LagomApplication which each lagom application extends
          *
          * while in test scenario, the ServiceClient is provided by TestServer present in the test kit
          * (com.lightbend.lagom.scaladsl.testkit.ServiceTest)
          *
          */
        val client = server.serviceClient.implement[MicroServiceTwoService]

        // testing endPointOne of Microservice two
        client.ping.invoke().map { response =>
          response must === ("Response from Microservice two")
        }
      }
  }
}
