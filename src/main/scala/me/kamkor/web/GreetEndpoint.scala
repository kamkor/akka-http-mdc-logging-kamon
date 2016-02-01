package me.kamkor.web


import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import me.kamkor.actor.GreetActor

import scala.concurrent.Future
import scala.concurrent.duration._


class GreetEndpoint(actorSystem: ActorSystem) extends Directives with LazyLogging {

  import GreetActor._
  import akka.pattern.ask

  private implicit val timeout = Timeout(3.seconds)

  // notice that greetActor has non default dispatcher
  private val greetActor: ActorRef =
    actorSystem.actorOf(GreetActor.props().withDispatcher("akka.greet-actor-dispatcher"), "greetActor")

  private def logRequest(requestId: String, msg: String): Unit = {
    // I am also logging requestId manually for testing/debugging purpouses of the solution
    logger.info("reqId[{}] thread[{}] {}", requestId, Thread.currentThread().getName(), msg)
  }

  val route =
    (get & path("greet" / Segment) & headerValueByName("request-id")) { (who, requestId) =>
      logRequest(requestId, "parsed request")

      // I am also passing requestId manually for testing/debugging purpouses of the solution
      val greetReplyF: Future[GreetReply] = (greetActor ? Greet(requestId, who)).mapTo[GreetReply]

      onSuccess(greetReplyF) { greetReply =>
        logRequest(requestId, "completing request")
        complete(greetReply.msg)
      }
    }

}
