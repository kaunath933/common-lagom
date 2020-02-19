package com.knoldus.example

import akka.Done
import com.knoldus.customer.api.CustomerDetails
import com.knoldus.example.command.{CreateCustomerCommand, DeleteCustomer, GetCustomerCommand}
import com.knoldus.example.event.{CustomerAdded, CustomerDeleted}
import com.knoldus.example.state.CustomerState
import com.knoldus.libs.command.Commands
import com.knoldus.libs.event.Events
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

class CustomerEntity extends PersistentEntity {

  override type Command = Commands[_]
  override type Event = Events
  override type State = CustomerState

  override def initialState = CustomerState(None)

  override def behavior: (CustomerState) => Actions = {
    case CustomerState(_) => Actions()
      .onCommand[CreateCustomerCommand, Done] {
        case (CreateCustomerCommand(cust), ctx, _) =>
          ctx.thenPersist(CustomerAdded(cust))(_ ⇒ ctx.reply(Done))
      }
      .onReadOnlyCommand[GetCustomerCommand, CustomerDetails] {
        case (GetCustomerCommand(id), ctx, state) =>
          ctx.reply(state.customer.getOrElse(CustomerDetails(id, "name not found ", "email not found")))
      }
      .onEvent {
        case (CustomerAdded(customer), _) =>
          CustomerState(Some(customer))
      }
      .onCommand[DeleteCustomer, Done] {
        case (DeleteCustomer(id), ctx, _) =>{
          val event = CustomerDeleted(id)

          ctx.thenPersist(event) { _ =>
            ctx.reply(Done)
          }
        }
      }.onEvent {
      case (_, state) =>
        state
    }
  }
}

object CustomerSerializerRegistry extends JsonSerializerRegistry {
  override def serializers = List(
    JsonSerializer[CustomerDetails],
    //Commands
    JsonSerializer[CustomerDetails],
    JsonSerializer[CreateCustomerCommand],
    JsonSerializer[GetCustomerCommand],

    //Events
    JsonSerializer[CustomerAdded],

    //state
    JsonSerializer[CustomerState]
  )

}
