package me.kamkor

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import me.kamkor.web.GreetEndpoint

object Main extends App {

  implicit val actorSystem = ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = actorSystem.dispatcher

  val greetEndpoint = new GreetEndpoint(actorSystem)

  val bindingFuture = Http().bindAndHandle(greetEndpoint.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine() // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => {
       actorSystem.shutdown()
    })

}
