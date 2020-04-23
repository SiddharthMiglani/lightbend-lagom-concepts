package org.icx.kafka.publisher.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import org.icx.kafka.publisher.api.model.Item
import play.api.libs.json.{Format, Json}

class ItemEntity extends PersistentEntity {

  override type Command = ItemCommand[_]
  override type Event = ItemEvent
  override type State = ItemState

  override def initialState: ItemState = ItemState(Option.empty, LocalDateTime.now().toString)

  override def behavior: Behavior = {
    case ItemState(item, _) => Actions().
      onCommand[PutItem, Done] {

        case (PutItem(item), ctx, state) =>

          ctx.thenPersist(ItemSavedEvent(item)) { _ =>
            ctx.reply(Done)
          }
      }.onEvent {
      case (ItemSavedEvent(item), state) =>
        ItemState(Option(item), LocalDateTime.now().toString)
    }
  }
}

// command
trait ItemCommand[T] extends ReplyType[T]

case class PutItem(item: Item) extends ItemCommand[Done]

object PutItem {
  implicit val formatter = Json.format[PutItem]
}

// event
object ItemEventTag {
  val INSTANCE = AggregateEventTag[ItemEvent]
}

sealed trait ItemEvent extends AggregateEvent[ItemEvent] {
  override def aggregateTag: AggregateEventTagger[ItemEvent] = ItemEventTag.INSTANCE
}

case class ItemSavedEvent(item: Item) extends ItemEvent

object ItemSavedEvent {
  implicit val fomatter = Json.format[ItemSavedEvent]
}

// state
case class ItemState(item: Option[Item], timestamp: String)

object ItemState {
  implicit val artifactStateFormat: Format[Item] = Json.format[Item]
}
