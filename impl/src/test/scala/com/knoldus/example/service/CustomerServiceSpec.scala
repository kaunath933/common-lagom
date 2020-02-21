package com.knoldus.example.service

import akka.Done
import com.knoldus.customer.api.{CustomerApi, CustomerDetails}
import com.knoldus.example.CustomerServiceApplication
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class CustomerServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  lazy val server = ServiceTest.startServer(ServiceTest.defaultSetup.withCassandra(true)) { ctx =>
    new CustomerServiceApplication(ctx) with LocalServiceLocator
  }

  lazy val client = server.serviceClient.implement[CustomerApi]

  override protected def beforeAll(): Unit = {
    server
    println("\n\n\n\n\n\n\n\n"+"inside beforeALlllll"+ "\n\n\n\n\n\n\n\n")
    val session: CassandraSession = server.application.cassandraSession

    createTable(session)

    //Add some fake data for testing purpose.
    populateData(session)

  }

  private def createTable(session: CassandraSession): Unit = {

    //Create table
    val createTable = session.executeCreateTable("""CREATE TABLE IF NOT EXISTS customer.customer (id text PRIMARY KEY, name text, email text)""".stripMargin)
    Await.result(createTable, 20 seconds)
  }

  private def populateData(session: CassandraSession): Unit = {
    val customer1 = CustomerDetails("1","Bob","bob@gmail.com")
    val insertProduct: Future[Done] = session.executeWrite("INSERT INTO product (id, name, email) VALUES (?, ?, ?)", customer1.id,
      customer1.name, customer1.email)
    Await.result(insertProduct, 20 seconds)
  }


  "Product service" should {
    val customer2 = CustomerDetails("1","Bob","bob@gmail.com")
    val actualResult = "name for 1  Bob"
    "should return product by id" in {
      client.getCustomerDetails("1").invoke().map { response =>
        println(response + "response")
        response should ===(actualResult)

      }
    }
  }

}
