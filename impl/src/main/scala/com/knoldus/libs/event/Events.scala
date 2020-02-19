package com.knoldus.libs.event

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}

//import play.api.libs.json.{Format, Json}

 trait Events extends AggregateEvent[Events] {
  override def aggregateTag: AggregateEventTagger[Events] = Events.Tag
}

object Events {
  val NumShards = 3
  val Tag: AggregateEventShards[Events] = AggregateEventTag.sharded[Events](NumShards)
}

