package org.icx.kafka.publisher.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import org.icx.kafka.publisher.api.KafkaPublisherService
import play.api.libs.ws.ahc.AhcWSComponents

class kafkaPublisherLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new KafkaPublisherApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new KafkaPublisherApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[KafkaPublisherService])
}

abstract class KafkaPublisherApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents
    with CassandraPersistenceComponents
    with LagomKafkaComponents
{
  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[KafkaPublisherService](wire[KafkaPublisherImpl])

  override lazy val jsonSerializerRegistry = ItemSerializerRegistry

  persistentEntityRegistry.register(wire[ItemEntity])
}


