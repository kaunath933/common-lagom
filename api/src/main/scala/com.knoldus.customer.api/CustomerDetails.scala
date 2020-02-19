package com.knoldus.customer.api

import play.api.libs.json.{Format, Json}

case class CustomerDetails(id: String, name: String, email: String)

object CustomerDetails {

  implicit val format: Format[CustomerDetails] = Json.format[CustomerDetails]
}
