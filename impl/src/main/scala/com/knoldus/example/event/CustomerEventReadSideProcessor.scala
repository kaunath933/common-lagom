package com.knoldus.example.event

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.knoldus.customer.api.CustomerDetails
import com.knoldus.libs.event.{Events, EventReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.{ExecutionContext, Future}

class CustomerEventReadSideProcessor(db:CassandraSession, readSide:CassandraReadSide)(implicit ec:ExecutionContext) extends EventReadSideProcessor {

  var addEntity: PreparedStatement = _

  var deleteEntity: PreparedStatement = _

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[Events] = readSide.builder[Events]("EventReadSidePreocessor")
    .setGlobalPrepare(() => createTable)
    .setPrepare(_ => prepareStatements())
    .setEventHandler[CustomerAdded](ese => addEntity(ese.event.customer))
    .setEventHandler[CustomerDeleted](ese => deleteEntity(ese.event.id))
    .build()

  def createTable(): Future[Done] = {
    db.executeCreateTable(
      """CREATE TABLE IF NOT EXISTS customerdatabase.customer (
        |id text PRIMARY KEY, name text, email text)""".stripMargin)
  }

  def prepareStatements(): Future[Done] =
    db.prepare("INSERT INTO customerdatabase.customer (id, name, email) VALUES (?, ?, ?)")
      .map { ps =>
        addEntity = ps
        Done
      }.map(_ => db.prepare("DELETE FROM customer where id = ?").map(ps => {
      deleteEntity = ps
      Done
    })).flatten

  def addEntity(customer: CustomerDetails): Future[List[BoundStatement]] = {
    val bindInsertCustomer: BoundStatement = addEntity.bind()
    bindInsertCustomer.setString("id", customer.id)
    bindInsertCustomer.setString("name", customer.name)
    bindInsertCustomer.setString("email", customer.email)
    Future.successful(List(bindInsertCustomer))
  }

  def deleteEntity(id: String): Future[List[BoundStatement]] = {
    val bindDeleteCustomer: BoundStatement = deleteEntity.bind()
    bindDeleteCustomer.setString("id", id)
    Future.successful(List(bindDeleteCustomer))
  }
}