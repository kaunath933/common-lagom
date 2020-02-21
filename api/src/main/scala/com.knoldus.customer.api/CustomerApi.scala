package com.knoldus.customer.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait CustomerApi extends Service {

  def getCustomerDetails(id: String): ServiceCall[NotUsed, String]

  def addCustomer(id: String, name: String, email: String): ServiceCall[NotUsed, String]

  def deleteCustomer(id: String): ServiceCall[NotUsed, Done]

  override final def descriptor: Descriptor = {
    import Service._
    named("common-lagom")
      .withCalls(
        restCall(Method.GET, "/api/details/get/:id", getCustomerDetails _),
        restCall(Method.POST, "/api/details/add/:id/:name/:email", addCustomer _),
        restCall(Method.DELETE, "/api/delete/:id", deleteCustomer _)
      ).withAutoAcl(true)

  }
}
