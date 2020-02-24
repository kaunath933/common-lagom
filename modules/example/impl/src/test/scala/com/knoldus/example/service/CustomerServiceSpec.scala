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
import scala.util.{Failure, Success}

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
  override protected def afterAll() = server.stop()

  private def createTable(session: CassandraSession): Unit = {

    val createKeyspace = session.executeWrite("CREATE KEYSPACE IF NOT EXISTS customerdatabase WITH replication = {'class': 'SimpleStrategy','replication_factor': '1'};")
    Await.result(createKeyspace, 10.seconds)

    //Create table
    val createTable = session.executeCreateTable("""CREATE TABLE IF NOT EXISTS customerdatabase.customer (id text PRIMARY KEY, name text, email text)""".stripMargin)
    Await.result(createTable, 10 seconds)
  }

  private def populateData(session: CassandraSession): Unit = {
    val customer = CustomerDetails("1","Bob","bob@gmail.com")
    val insertProduct: Future[Done] = session.executeWrite("INSERT INTO customerdatabase.customer (id, name, email) VALUES (?, ?, ?)", customer.id,
      customer.name, customer.email)

//    insertProduct.onComplete{
//    case Success(value) => println("value is" +value)
//    case Failure(exception) => println("exception is" +exception)
//  }
    Await.result(insertProduct, 10 seconds)
}


  "/api/details/get/:id" should {
    val expectedResult = CustomerDetails("1","Bob","bob@gmail.com")
    "should return customer name by id" in {
      client.getCustomerDetails("1").invoke().map { response =>
        println(response + "response")
        response should ===(expectedResult)

      }
    }
  }

}
