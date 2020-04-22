package org.icx.mcrsrvctwo.impl


import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.icx.mcrsrvctwo.api.MicroServiceTwoService
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpec

/**
  * For this to make more sense, first go through MicroServiceTwoBasicSpec
  *
  * MicroServiceTwoBasicSpec explains in detail all the necessary things to test a service
  * Only thing is that it creates a server for each test, which might not always be a good idea
  * So, this class just explains how to create a server and client and use them in all tests
  */
class MicroServiceTwoAdvancedSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) { ctx =>
    new MicroServiceTwoApplication(ctx) with LocalServiceLocator
  }
  lazy val client = server.serviceClient.implement[MicroServiceTwoService]

  "Microservice two" should {
    "respond from endPointOne" in {
      client.ping.invoke().map { response =>
        response must === ("Response from Microservice two")
      }
    }
  }

  protected override def beforeAll() = server

  protected override def afterAll() = server.stop()

}
