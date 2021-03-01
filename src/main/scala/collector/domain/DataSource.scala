package collector.domain

import java.io.{BufferedReader, InputStreamReader}
import scala.util.chaining.scalaUtilChainingOps

object DataSource {

  def spawn(cs: String*) = Runtime
    .getRuntime
    .exec(cs.toArray)

  def spawnBlackBox = spawn("/bin/sh", "-c", "~/Downloads/blackbox")

  def create =
    spawnBlackBox
      .pipe(_.getInputStream)
      .pipe(new InputStreamReader(_))
      .pipe(new BufferedReader(_))

}
