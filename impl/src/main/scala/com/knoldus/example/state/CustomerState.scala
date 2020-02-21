package com.knoldus.example.state

import com.knoldus.customer.api.CustomerDetails
import play.api.libs.json.{Format, Json}

case class CustomerState(customer: Option[CustomerDetails])

object CustomerState {
  implicit val format: Format[CustomerState] = Json.format
}
