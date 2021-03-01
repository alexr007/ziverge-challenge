package collector.aa

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import collector.aa.CollectorActor.RequestState
import collector.domain.Domain.Counter
import upickle.default._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

class CollectorRouter(service: ActorRef[CollectorActor.Command])(implicit val system: ActorSystem[_]) {

  /** for ask pattern */
  private implicit val timeout = Timeout(5.second)
  private implicit val ec: ExecutionContextExecutor = system.executionContext

  private def obtainStateFromActor = service
    .ask(me => RequestState(me))
    .map(write[Counter](_))

  val routes: Route =
    pathPrefix("state") {
      get {
        complete(obtainStateFromActor)
      }
    }

}
