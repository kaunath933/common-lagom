package com.knoldus.example.event

import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.knoldus.customer.api.CustomerDetails
import com.knoldus.libs.constants.QueryConstants
import com.knoldus.libs.event.{EventReadSideProcessor, Events}
import com.lightbend.lagom.scaladsl.persistence.ReadSideProcessor
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.{ExecutionContext, Future}

case class CustomerEventReadSideProcessor(db: CassandraSession, readSide: CassandraReadSide)(implicit ec: ExecutionContext) extends EventReadSideProcessor {

  var addEntity: PreparedStatement = _

  var deleteEntity: PreparedStatement = _

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[Events] = readSide.builder[Events]("EventReadSidePreocessor")
    .setGlobalPrepare(() => createTable)
    .setPrepare(_ => prepareStatements())
    .setEventHandler[CustomerAdded](ese => addEntity(ese.event.customer))
    .setEventHandler[CustomerDeleted](ese => deleteEntity(ese.event.id))
    .build()

  override def createTable(): Future[Done] = {
    db.executeCreateTable(QueryConstants.CREATE_TABLE
      .stripMargin)
  }

  def prepareStatements(): Future[Done] =
    db.prepare(QueryConstants.INSERT_PRODUCT)
      .map { ps =>
        addEntity = ps
        Done
      }.map(_ => db.prepare(QueryConstants.DELETE_PRODUCT).map(ps => {
      deleteEntity = ps
      Done
    })).flatten

  def addEntity(entity: CustomerDetails): Future[List[BoundStatement]] = {
    val bindInsertCustomer: BoundStatement = addEntity.bind()
    bindInsertCustomer.setString("id", entity.id)
    bindInsertCustomer.setString("name", entity.name)
    bindInsertCustomer.setString("email", entity.email)
    Future.successful(List(bindInsertCustomer))
  }

  def deleteEntity(entity: String): Future[List[BoundStatement]] = {
    val bindDeleteCustomer: BoundStatement = deleteEntity.bind()
    bindDeleteCustomer.setString("id", entity)
    Future.successful(List(bindDeleteCustomer))
  }
}