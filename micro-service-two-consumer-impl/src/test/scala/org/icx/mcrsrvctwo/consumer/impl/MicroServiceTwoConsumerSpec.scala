package org.icx.mcrsrvctwo.consumer.impl

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.icx.mcrsrvctwo.api.MicroServiceTwoService
import org.icx.mcrsrvctwo.api.model.Item
import org.icx.mcrsrvctwo.consumer.api.MicroServiceTwoConsumerService
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * This test explains:
  *   how to test a service that consumes other service
  *   and, how to test a streaming service
  */
class MicroServiceTwoConsumerSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  val MOCKED_RESPONSE = "Mocked response from microservice two"
  val MOCKED_STREAM ="I've been invoked from inside consumer of microservice two"

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup) { ctx =>

    /**
      * In a simple test server creation should end by mixin in LocalServiceLocator. But in here, we are overriding
      * the microServiceTwo implementation with a mocked implementation.
      *
      * Strange thing which is not understandable is that in actual implementation of MicroServiceTwoConsumerApplication,
      * microServiceTwo holds a client to MicroServiceTwoService, then how is it that initializing it with a Service
      * (MicroServiceTwoService) object is working fine ???
      */
    new MicroServiceTwoConsumerApplication(ctx) with LocalServiceLocator {
      override lazy val microServiceTwo = new MicroServiceTwoService {

        override def ping = ServiceCall { _ =>
          Future.successful(MOCKED_RESPONSE)
        }

        override def streamingEndPoint(interval: Int): ServiceCall[String, Source[String, NotUsed]] = ServiceCall { MOCKED_STREAM =>
          Future.successful(
            Source.tick(500.milliseconds, interval.milliseconds, MOCKED_STREAM).mapMaterializedValue( _ => NotUsed)
          )
        }

        override def nonPrimitiveResponse: ServiceCall[Long, Item] = ???

        override def playWithReqResHeaders: ServiceCall[NotUsed, String] = ???
      }
    }
  }
  lazy val client = server.serviceClient.implement[MicroServiceTwoConsumerService]

  "Microservice two consumer" should {
    "respond from consumeEndPointOne" in {
      client.ping.invoke().map { response =>
        response must === (s"Microservice two says: $MOCKED_RESPONSE")
      }
    }
  }

  "Microservice two consumer" should {
    "respond from consumeEndPointThree" in {
      implicit val system = ActorSystem("not-sure-why-this-is-required")
      client.streamingEndPoint.invoke().map { output =>
        // val probe = output.runWith(TestSink.probe(server.actorSystem))
        val probe = output.runWith(TestSink.probe(server.actorSystem))
        probe.request(10)
        probe.expectNext(MOCKED_STREAM)
        probe.expectNext(MOCKED_STREAM)
        probe.expectNext(MOCKED_STREAM)
        probe.cancel
        succeed
      }
    }
  }

  protected override def beforeAll() = server

  protected override def afterAll() = server.stop()

}
