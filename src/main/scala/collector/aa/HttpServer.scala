package collector.aa

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

object HttpServer {

  def start(routes: Route, port: Int = 8080)(implicit system: ActorSystem[_]) =
    Http()
      .newServerAt("localhost", port)
      .bind(routes)

}
