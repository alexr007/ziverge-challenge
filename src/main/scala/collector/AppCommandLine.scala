package collector

import collector.domain.DataSource
import collector.domain.Domain.{Frame, Item}

import java.io.BufferedReader
import java.time.Instant
import scala.util.chaining.scalaUtilChainingOps

object AppCommandLine extends App {

  val reader: BufferedReader = DataSource.create
  val lines: Iterable[String] = LazyList.continually(reader.readLine)
  val items: Iterable[Item] = lines.flatMap(Item.readOpt)

  val frameLifespan = 20
  val now = Instant.now().getEpochSecond
  val first = Frame.next(now, frameLifespan)

  items.foldLeft(first) { (frame, item) =>
    frame.collect(item)
      .tap(println)
  }

}
