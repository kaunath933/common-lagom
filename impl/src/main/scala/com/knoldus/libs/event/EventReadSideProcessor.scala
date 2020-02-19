package com.knoldus.libs.event

import akka.Done
import com.datastax.driver.core.BoundStatement
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

import scala.concurrent.Future

abstract class EventReadSideProcessor extends ReadSideProcessor[Events] {

  def aggregateTags: Set[AggregateEventTag[Events]] = Events.Tag.allTags

  def createTable(): Future[Done]

  def prepareStatements(): Future[Done]

//  def addEntity[T](entity: T): Future[List[BoundStatement]]
//
//  def deleteEntity[T](entity: T): Future[List[BoundStatement]]
}
