package collector

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.scaladsl.{Flow, Sink, Source}
import collector.aa.CollectorActor.NewItemProvided
import collector.aa.{CollectorActor, CollectorRouter, HttpServer}
import collector.domain.DataSource
import collector.domain.Domain.{Frame, Item}

import java.io.BufferedReader
import java.time.Instant
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * http://localhost:8080/state
 */
object AppStreamingAndHttp extends App {
  /** system setup */
  implicit val as: ActorSystem[SpawnProtocol.Command] = ActorSystem(SpawnProtocol(), "collector-app")
  implicit val ec: ExecutionContext = as.executionContext

  /** external data source */
  val reader: BufferedReader = DataSource.create
  val lines: Iterable[String] = LazyList.continually(reader.readLine)
  val items: Iterable[Item] = lines.flatMap(Item.readOpt)

  /** initial state */
  val frameLifespan = 30 // seconds
  val now = Instant.now().getEpochSecond
  val firstFrame = Frame.next(now, frameLifespan)

  /** collector actor */
  val collectorRef = as.systemActorOf(CollectorActor(firstFrame), "collector-actor")

  /** akka streaming */
  val source = Source.fromIterator { () => items.iterator }
  val toMessage = Flow.fromFunction { x: Item => NewItemProvided(x) }
  val toActor = Flow.fromFunction { x: NewItemProvided => collectorRef ! x }

  val stream = source
    .via(toMessage)
    .via(toActor)
    .runWith(Sink.ignore)

  val router = new CollectorRouter(collectorRef)
  val server = HttpServer.start(router.routes)

  stream.onComplete {
    case Success(_) =>
      as.log.info(s"Done")
      as.terminate()
    case Failure(x) =>
      as.log.error(s"Done with errors $x")
      as.terminate()
  }

  server.onComplete {
    case Success(ServerBinding(la)) =>
      as.log.info("Server started at http://{}:{}/", la.getHostString, la.getPort)
    case Failure(ex) =>
      as.log.error("Server failed to start", ex)
      as.terminate()
  }

}
