package org.icx.kafka.publisher.api.model

import play.api.libs.json.Json

case class Item(id: Long, name: String)

object Item { implicit val entityFormatter = Json.format[Item] }
