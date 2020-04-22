package org.icx.kafka.publisher.impl.events

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger}
import org.icx.kafka.publisher.api.model.SomeEntity
import play.api.libs.json.Json


object EntityEventTag {
  val INSTANCE = AggregateEventTag[EntityEvent]
}

sealed trait EntityEvent extends AggregateEvent[EntityEvent] {
  override def aggregateTag: AggregateEventTagger[EntityEvent] = EntityEventTag.INSTANCE
}

case class EntitySavedEvent(theEntity: SomeEntity) extends EntityEvent
object TheEntitySaved { implicit val fomatter = Json.format[EntitySavedEvent] }
