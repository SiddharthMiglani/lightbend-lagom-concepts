package org.icx.mcrsrvctwo.impl

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.ResponseHeader
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import org.icx.mcrsrvctwo.api.MicroServiceTwoService
import org.icx.mcrsrvctwo.api.model.Item

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * @param ec : an application vide execution context for threads i.e. Futures to work
  */
class MicroServiceTwoImpl(implicit ec: ExecutionContext) extends MicroServiceTwoService {

  override def ping: ServiceCall[NotUsed, String] = ServiceCall { _ =>
    Future.successful("Response from micro-service two")
  }

  override def nonPrimitiveResponse: ServiceCall[Long, Item] = ServiceCall { id =>
    Future.successful(Item(id, "Item number " + id))
  }

  override def streamingEndPoint(interval: Int): ServiceCall[String, Source[String, NotUsed]] = ServiceCall { msg =>
    Future.successful(
      Source.tick(500.milliseconds, interval.milliseconds, msg).mapMaterializedValue( _ => NotUsed)
    )
  }

  /**
    * This implementation explains the benefits of using ServerService call.
    *
    * The fact that:
    *   it has got access to request header and
    *   that it has a compose method
    *  add a lot of value.
    *
    * The necessary part of it is just the code starting from ServerServiceCall. loggedServiceCall is just a
    * wrapper around it which takes in a ServerServiceCall and outputs a ServerServiceCall. Idea of methods like
    * loggedServiceCall is to add value to an existing ServerServiceCall
    *
    */
  override def playWithReqResHeaders: ServerServiceCall[NotUsed, String] = loggedServiceCall(
    ServerServiceCall { (requestHeader, _) =>
      val user = requestHeader.principal
        .map(_.getName)
        .getOrElse("No one")

      val responseHeader = ResponseHeader.Ok.withHeader("Server", "Microservice Two")
      val response = s"Response from Microservice two for $user"

      Future.successful((responseHeader, response))
    }
  )

  // composes a logged serverServiceCall from an existing serverServiceCall
  def loggedServiceCall[Request, Response](serviceCall: ServerServiceCall[Request, Response]) =
    ServerServiceCall.compose { requestHeader =>
      println(s"Received ${requestHeader.method} ${requestHeader.uri}")
      serviceCall
    }
}
