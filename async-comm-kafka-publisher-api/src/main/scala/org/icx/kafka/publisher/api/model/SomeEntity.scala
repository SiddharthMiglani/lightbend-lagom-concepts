package org.icx.kafka.publisher.api.model

import play.api.libs.json.Json

case class SomeEntity(id: Long, name: String)

object SomeEntity { implicit val entityFormatter = Json.format[SomeEntity] }
