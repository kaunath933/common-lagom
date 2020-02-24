package com.knoldus.example.event

import com.knoldus.customer.api.CustomerDetails
import com.knoldus.libs.event.Events
import play.api.libs.json.{Format, Json}

case class CustomerAdded(customer: CustomerDetails) extends Events

object CustomerAdded {
  implicit val format: Format[CustomerAdded] = Json.format
}

case class CustomerDeleted(id: String) extends Events

object CustomerDeleted {
  implicit val format: Format[CustomerDeleted] = Json.format
}