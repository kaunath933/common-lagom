package com.knoldus.example.service

import akka.{Done, NotUsed}
import com.knoldus.customer.api.{CustomerApi, CustomerDetails}
import com.knoldus.example.CustomerEntity
import com.knoldus.example.command.{CreateCustomerCommand, DeleteCustomer, GetCustomerCommand}
import com.knoldus.libs.command.Commands
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}

import scala.concurrent.ExecutionContext

class CustomerServiceImpl(persistentEntityRegistry: PersistentEntityRegistry)(implicit ec: ExecutionContext) extends CustomerApi {

  override def addCustomer(id: String, name: String, email: String): ServiceCall[NotUsed, String] = {
    ServiceCall { _ =>
      val cust = CustomerDetails(id, name, email)
      ref(cust.id).ask(CreateCustomerCommand(cust)).map {
        case Done => {
          s"$name, you ae registered "
        }
      }
    }
  }

  override def getCustomerDetails(id: String): ServiceCall[NotUsed, String] = {
    ServiceCall { _ =>
      ref(id).ask(GetCustomerCommand(id)).map(cust =>
        s"name for $id is ${cust.name}")
    }
  }

  override def deleteCustomer(id: String): ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    ref(id).ask(DeleteCustomer(id)).map(_ => {
      Done.getInstance()
    })

  }

  def ref(id: String): PersistentEntityRef[Commands[_]] = {
    persistentEntityRegistry
      .refFor[CustomerEntity](id)
  }

}
