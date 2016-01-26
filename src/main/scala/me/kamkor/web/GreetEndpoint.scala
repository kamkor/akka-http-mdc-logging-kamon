package me.kamkor.web


import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import kamon.Kamon
import kamon.trace.logging.MdcKeysSupport
import kamon.trace.{TraceLocal, Tracer}
import me.kamkor.actor.GreetActor

import scala.concurrent.Future
import scala.concurrent.duration._


class GreetEndpoint(actorSystem: ActorSystem) extends Directives with MdcKeysSupport with LazyLogging {

  import GreetActor._
  import akka.pattern.ask

  private implicit val timeout = Timeout(3.seconds)

  // notice that greetActor has non default dispatcher
  private val greetActor: ActorRef =
    actorSystem.actorOf(GreetActor.props().withDispatcher("akka.greet-actor-dispatcher"), "greetActor")

  private def logRequest(requestId: String, msg: String): Unit = {
    // when using classic logger (not logger from akka), use MdcKeysSupport withMdc to propagate mdc from current context
    withMdc {
      logger.info("thread[{}] reqId[{}] - GreetEndpoint ### {}", Thread.currentThread().getName(), requestId, msg)
    }
  }

  val route =
    (get & path("greet" / Segment) & headerValueByName("request-id")) { (who, requestId) =>
      val context = Kamon.tracer.newContext("MDC")

      Tracer.withContext(context) {
        TraceLocal.storeForMdc("requestId", requestId)
        logRequest(requestId, "parsed request")

        // context will be propagated to actor by kamon
        val greetReplyF: Future[GreetReply] = (greetActor ? Greet(requestId, who)).mapTo[GreetReply]

        onSuccess(greetReplyF) { greetReply =>
          // context is unfortunately lost here, it must be set again
          Tracer.withContext(context) {
            logRequest(requestId, "completing request")
          }
          context.finish()
          complete(greetReply.msg)
        }
      }

    }

}
