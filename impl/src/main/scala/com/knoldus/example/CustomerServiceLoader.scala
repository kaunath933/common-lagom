package com.knoldus.example

import com.knoldus.customer.api.CustomerApi
import com.knoldus.example.event.CustomerEventReadSideProcessor
import com.knoldus.example.service.CustomerServiceImpl
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

class CustomerServiceLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new CustomerServiceApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new CustomerServiceApplication(context) with LagomDevModeComponents
}

abstract class CustomerServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer = serverFor[CustomerApi](wire[CustomerServiceImpl])

  //Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = CustomerSerializerRegistry

  // Register the lagom-persistent-entity-demo persistent entity
  persistentEntityRegistry.register(wire[CustomerEntity])
  readSide.register(wire[CustomerEventReadSideProcessor])

}
