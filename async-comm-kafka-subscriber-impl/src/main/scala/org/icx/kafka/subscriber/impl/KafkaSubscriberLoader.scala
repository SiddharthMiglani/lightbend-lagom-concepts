package org.icx.kafka.subscriber.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import org.icx.kafka.publisher.api.KafkaPublisherService
import org.icx.kafka.subscriber.api.KafkaSubscriberService
import play.api.libs.ws.ahc.AhcWSComponents

class KafkaSubscriberLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new KafkaSubscriberApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new KafkaSubscriberApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[KafkaSubscriberService])
}

abstract class KafkaSubscriberApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with CassandraPersistenceComponents
    with LagomKafkaComponents
{
  lazy val twitterService = serviceClient.implement[KafkaPublisherService]

  override lazy val lagomServer: LagomServer = serverFor[KafkaSubscriberService](wire[KafkaSubscriberImpl])

  override lazy val jsonSerializerRegistry = ItemSerializerRegistry

  persistentEntityRegistry.register(wire[ItemEntity])
}
