package org.icx.mcrsrvctwo.api.model

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Item(id: Long, name: String)

object Item {
  implicit val defaultFormatter = Json.format[Item]

  val customFormatter: Format[Item] = {
    ((__ \ "identifier").format[Long] ~ (__ \ "name").format[String]) (Item.apply, unlift(Item.unapply))
  }
}
