package com.knoldus.example.command

import akka.Done
import com.knoldus.customer.api.CustomerDetails
import com.knoldus.libs.command.Commands
import play.api.libs.json.{Format, Json}

case class CreateCustomerCommand(customer: CustomerDetails) extends Commands[Done]

object CreateCustomerCommand {
  implicit val format: Format[CreateCustomerCommand] = Json.format
}

case class GetCustomerCommand(id: String) extends Commands[CustomerDetails]

object GetCustomerCommand {
  implicit val format: Format[GetCustomerCommand] = Json.format
}

case class DeleteCustomerCommand(id: String) extends Commands[Done]

object DeleteCustomerCommand {
  implicit val format: Format[DeleteCustomerCommand] = Json.format
}

