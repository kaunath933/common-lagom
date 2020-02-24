package com.knoldus.libs.event

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}

import scala.concurrent.Future

trait EventReadSideProcessor extends ReadSideProcessor[Events] {

  def db: CassandraSession

  val readSide: CassandraReadSide

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[Events]

  def aggregateTags: Set[AggregateEventTag[Events]] = Events.Tag.allTags

  def createTable(): Future[Done]

  def prepareStatements(): Future[Done]

}
