package org.icx.mcrsrvctwo.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import org.icx.mcrsrvctwo.api.model.Item
import org.icx.mcrsrvctwo.api.serializers.TextMessageSerializer
import play.api.libs.json.JsValue

trait MicroServiceTwoService extends Service {
  /**
    * @return a handler to the service which can be invoked [1]
    *
    *  The most basic request with a Strict request type and Strict response type [2]
    */
  def ping: ServiceCall[NotUsed, String]

  /**
    * Strict request type and Strict response type [2]
    * Strict message type can be any scala object which is JSON serializable [3]
    *
    * Notes:
    * Notice that return type Service call has a request type which is long
    *
    * This becomes the parameter for invoke method available on the ServiceCall which means that if you use a
    * client object to call this service it will be called like this -> someClient.endPointTwo.invoke(2)
    *
    * But in case you are using some REST client software like postman this parameter goes in the request body.
    *
    * @return a handler to the service which can be invoked
    */
  def nonPrimitiveResponse: ServiceCall[Long, Item]

  /**
    * Strict request type and Stream response type [2]
    * Stream message type returns an object of type akka.stream.scaladsl.Source [4]
    * (imagine it as a source of some stream)
    *
    * Notes:
    * Notice that this call has an Int parameter and the service call handler that it returns has a String request type
    *
    * What this means is that Int is a parameter to endPointThree method and String is a parameter to invoke method
    * available on service call. So, if you use a client object to invoke this service, the way to do it is this ->
    * someClient.endPointThree(1000).invoke("someMessage")
    *
    * And in case you are using some REST client software like postman then "someMessage" goes to request body
    * and 1000 goes to URL parameter like this -> /microservicetwo/endPointThree/:1000
    *
    * @return a handler to the service which can be invoked
    */
  def streamingEndPoint(interval: Int): ServiceCall[String, Source[String, NotUsed]]

  /**
    * Strict request type and Strict response type [2]
    * This endpoint is to understand accessing request headers and creating response headers [5]
    *
    * @return a handler to the server service which can be invoked [5]
    */
  def playWithReqResHeaders: ServiceCall[NotUsed, String]

  /**
    * Strict request type and Strict response type [2]
    * This one is to create
    * @return a handler to the server service which can be invoked [5]
    */

  // def customizedJSONSerializer: ServiceCall[Long, Item]
  // def pingFlexibly: ServiceCall[NotUsed, String]

  override final def descriptor: Descriptor = {
    import Service._

    named("micro-service-two")
      .withCalls(
        /* ---------------- various ways to define calls (call identifiers) ----------------------- */
        // a simple call identifier where method name is used to route calls to this
        call(ping),
        // named call identifier where you can provide a name to be used to route calls to this
        namedCall("ping-microservice-two", ping),
        // REST call identifier where you can use both PATH and Request method to route calls to this
        // in normal path based call identifier as above, Lagom decides the semantic i.e. appropriate Request method
        restCall(Method.GET, "/microservicetwo/ping", ping),
        // path based call identifier where a URI path and query strings are used to route calls to this
        // path can contain dynamic parts and query strings like "/order/:orderId/item/:itemId?pageNo&pageSize"
        pathCall("/microservicetwo/ping", ping),

        /* -------------------- endpoints demonstrating various concepts --------------------------- */
        pathCall("/microservicetwo/nonPrimitiveResponse", nonPrimitiveResponse),
        pathCall("/microservicetwo/streamingEndPoint/:interval", streamingEndPoint _),
        pathCall("/microservicetwo/playWithReqResHeaders", playWithReqResHeaders),

        /* ---------------- endpoints demonstrating custom message serialization -------------------- */

        /**
          * message serializers are implicitly available to each type of call identifier described above
          * call(sayHello)(MessageSerializer.StringMessageSerializer, MessageSerializer.StringMessageSerializer)
          *
          * but still it is possible to define and explicitly pass custom serializers [6]
          */

        // explicitly passing custom json serializer
        pathCall("/microservicetwo/nonPrimitiveResponse/customJSONSerializer", nonPrimitiveResponse)(
          // Request serializer
          implicitly[MessageSerializer[Long, ByteString]],

          // Response serializer
          MessageSerializer.jsValueFormatMessageSerializer(
            implicitly[MessageSerializer[JsValue, ByteString]],
            Item.customFormatter
          )
        ),

        // explicitly passing custom text serializer
        pathCall("/microservicetwo/ping/customMsgSerializer", ping)(
          // Request serializer
          implicitly[MessageSerializer[NotUsed, ByteString]],

          // Response serializer
          new TextMessageSerializer
        )
      )
      .withAutoAcl(true) // to generate Service ACLs from each call’s pathPattern (server side service discovery)
  }

  /**
    * [1] Service call:
    *     Trait ServiceCall looks like this (notice that it returns a Future of type Response):
    *
    *          trait ServiceCall[Request, Response] {
    *            def invoke(request: Request): Future[Response]
    *          }
    *
    *     A response wrapped in future is means that it is not yet available for use
    *     It is an anchor to a computation that will complete sometime in future
    *     Making type of every Service call's response as a Future, Lagom enforces asynchronous code
    *
    * [2] Message Types:
    *     Each service call has a request message type and a response message type,
    *     which can either be: Strict or Stream (more on that later)
    *
    *     When a request or response message is not used akka.NotUsed can be used
    *
    * [3] Message serialization:
    *     Message serializers for requests and responses are provided using type classes.
    *
    *     Each of the call, namedCall, pathCall and restCall methods take an implicit MessageSerializer
    *     for each of the request and response messages.
    *
    *     Out of the box Lagom provides a serializer for String messages,
    *     as well as serializers that implicitly convert a Play JSON Format type class to a message serializer.
    *
    *     [3.1] Using Play JSON for message serialization:
    *           By defining the format on any companion object (check Item class),
    *           we can ensure that this format will be automatically used whenever it is required,
    *           due to Scala’s implicit scoping rules.
    *
    *           This means that aside from declaring the format,
    *           no further work needs to be done to ensure that this format will be used for the MessageSerializer.
    *
    *           Note that if your case class references another, non primitive type, such as another case class,
    *           you’ll need to also define a format for that case class.
    *
    * [4] Stream message types/Streaming endpoints/Bidirectional streams:
    *     This is in essence a streaming endpoint. It is different from a standard request response system
    *     which is synchronous (happening at same time). It is asynchronous streaming and handling of messages.
    *
    *     Lagom will choose an appropriate transport for the stream, typically, this will be WebSockets.
    *     The WebSocket protocol supports bidirectional streaming, so is a good general purpose option for streaming.
    *
    *     When only one of the request or response message is streamed, Lagom will implement the sending and receiving
    *     of the strict message by sending or receiving a single message, and then leaving the WebSocket open until
    *     the other direction closes. Otherwise, Lagom will close the WebSocket when either direction closes.
    *
    * [5] Handling request and response headers:
    *     ServerServiceCall is an interface that extends ServiceCall, and provides an additional method,
    *     invokeWithHeaders.
    *
    *     This is different from the regular invoke method because in addition to the Request parameter,
    *     it also accepts a RequestHeader parameter. And rather than returning a Future[Response],
    *     it returns a Future[(ResponseHeader, Response)]. Hence it allows you to handle the request header,
    *     and send a custom response header.
    *
    *     Having access to request header assist in lot of things major being authentication
    *
    *     Check implementation class for more details and to see this in action
    *
    * [6] <-- add about serializers here -->
    */
}
