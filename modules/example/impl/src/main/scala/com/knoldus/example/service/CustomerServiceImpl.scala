package com.knoldus.example.service

import akka.{Done, NotUsed}
import com.knoldus.customer.api.{CustomerApi, CustomerDetails}
import com.knoldus.example.CustomerEntity
import com.knoldus.example.command.{CreateCustomerCommand, DeleteCustomerCommand, GetCustomerCommand}
import com.knoldus.libs.command.Commands
import com.knoldus.libs.constants.QueryConstants
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{ExceptionMessage, NotFound, TransportErrorCode}
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.persistence.{PersistentEntityRef, PersistentEntityRegistry}

import scala.concurrent.ExecutionContext

class CustomerServiceImpl(persistentEntityRegistry: PersistentEntityRegistry,session:CassandraSession)(implicit ec: ExecutionContext) extends CustomerApi {


  override def getCustomerDetails(id: String): ServiceCall[NotUsed, CustomerDetails] = ServiceCall { _ =>
    session.selectOne(QueryConstants.GET_PRODUCT, id).map {
      case Some(row) => CustomerDetails.apply(row.getString("id"), row.getString("name"), row.getString("email"))
      case None => throw new NotFound(TransportErrorCode.NotFound, new ExceptionMessage("Customer Id Not Found",
        "Customer with this customer id does not exist"))
    }
  }

  override def getAllCustomers(): ServiceCall[NotUsed, List[CustomerDetails]] = ServiceCall { _ =>
    session.selectAll(QueryConstants.GET_ALL_PRODUCTS).map {
      row =>row.map(customer => CustomerDetails(customer.getString("id"), customer.getString("name"), customer.getString("email"))).toList
    }
  }


  override def addCustomer(): ServiceCall[CustomerDetails, String] = {
    ServiceCall { request =>
      ref(request.id).ask(CreateCustomerCommand(request)).map {
        case Done => {
          s"${request.name}, you are registered "
        }
      }
    }
  }







//  override def getCustomerDetails(id: String): ServiceCall[NotUsed, String] = {
//    ServiceCall { _ =>
//      ref(id).ask(GetCustomerCommand(id)).map(cust =>
//        s"name for $id  ${cust.name}")
//    }
//  }

  override def deleteCustomer(id: String): ServiceCall[NotUsed, Done] = ServiceCall { _ =>
    ref(id).ask(DeleteCustomerCommand(id)).map(_ => {
      Done.getInstance()
    })

  }

  def ref(id: String): PersistentEntityRef[Commands[_]] = {
    persistentEntityRegistry
      .refFor[CustomerEntity](id)
  }

}
