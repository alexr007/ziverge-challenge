package collector.domain

import scala.util.Try

object Domain {

  import upickle.default.{macroRW, read, ReadWriter => RW}

  /** event representation */
  case class Item(event_type: String, data: String, timestamp: Long)

  object Item {
    def readOpt(s: String) = Try(read[Item](s)).toOption

    /** JSON reader-writer */
    implicit val rw: RW[Item] = macroRW
  }

  /** counter implementation */
  case class Counter(data: Map[String, Map[String, Int]]) {
    def count(item: Item): Counter = Counter(data.updatedWith(item.event_type) {
      case None => Some(Map(item.data -> 1))
      case x => x.map(_.updatedWith(item.data) {
        case None => Some(1)
        case n => n.map(_ + 1)
      })
    })
  }

  object Counter {
    def empty = Counter(Map.empty)

    def startNew(item: Item) = Counter.empty.count(item)

    /** JSON reader-writer */
    implicit val rw: RW[Counter] = macroRW
  }

  /** frame representation */
  case class Frame(started: Long, cnt: Counter, lifeSpan: Long) {

    def isFrameExpired(at: Long) = at > started + lifeSpan

    def collect(item: Item): Frame = isFrameExpired(item.timestamp) match {
      /** start new one */
      case true => copy(started = item.timestamp, cnt = Counter.startNew(item))

      /** keep collecting */
      case false => copy(cnt = cnt.count(item))
    }

  }

  object Frame {
    def next(timestamp: Long, length: Long) = Frame(timestamp, Counter.empty, length)
  }

}
