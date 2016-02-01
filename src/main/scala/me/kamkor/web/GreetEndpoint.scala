package me.kamkor.web


import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import kamon.trace.TraceLocal
import kamon.trace.logging.MdcKeysSupport
import me.kamkor.actor.GreetActor
import me.kamkor.directives.TracingDirectives

import scala.concurrent.Future
import scala.concurrent.duration._


class GreetEndpoint(actorSystem: ActorSystem) extends Directives with TracingDirectives with MdcKeysSupport with LazyLogging {

  import GreetActor._
  import akka.pattern.ask

  private implicit val timeout = Timeout(3.seconds)

  // notice that greetActor has non default dispatcher
  private val greetActor: ActorRef =
    actorSystem.actorOf(GreetActor.props().withDispatcher("akka.greet-actor-dispatcher"), "greetActor")

  private def logRequest(requestId: String, msg: String): Unit = {
    // when using classic logger (not logger from akka), use MdcKeysSupport withMdc to propagate mdc from current context
    withMdc {
      // I am also logging requestId manually for testing/debugging purpouses of the solution
      logger.info("reqId[{}] thread[{}] {}", requestId, Thread.currentThread().getName(), msg)
    }
  }

  val route =
    traceContextAwareRoute { // this directive make sure that TraceContext is available in the route below
      (get & path("greet" / Segment) & headerValueByName("request-id")) { (who, requestId) =>
        TraceLocal.storeForMdc("requestId", requestId)
        logRequest(requestId, "parsed request")

        // context will be propagated to actor by kamon
        // I am also passing requestId manually for testing/debugging purpouses of the solution
        val greetReplyF: Future[GreetReply] = (greetActor ? Greet(requestId, who)).mapTo[GreetReply]

        onSuccess(greetReplyF) { greetReply =>
          logRequest(requestId, "completing request")
          complete(greetReply.msg)
        }
      }
    }

}
