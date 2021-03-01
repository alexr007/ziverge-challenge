package collector.aa

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import collector.domain.Domain.{Counter, Frame, Item}

/**
 * actor to handle our state
 * link to our implementation
 */
object CollectorActor {

  sealed trait Command
  case class RequestState(sender: ActorRef[Counter]) extends Command
  case class NewItemProvided(item: Item) extends Command

  def apply(frame: Frame)(implicit system: ActorSystem[_]): Behavior[Command] =
    Behaviors.receiveMessage {

      case RequestState(sender) =>
        system.log.info("> Got state request (http)")
        sender ! frame.cnt
        Behaviors.same

      case NewItemProvided(item) =>
        system.log.info(s"> Got new Item: $item")
        apply(frame.collect(item))

    }

}
