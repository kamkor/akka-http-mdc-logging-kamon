package me.kamkor.actor

import java.util.concurrent.Executors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.concurrent.{ExecutionContext, Future}

object GreetActor {

  // each msg has explicit requestId, to make it possible to check that mdc has correct request id. I will implement tests later.

  final case class Greet(requestId: String, who: String)

  final case class GreetReply(requestId: String, msg: String)

  private final case class GeneratedGreet(requestId: String, msg: String, sendTo: ActorRef)

  def props() = Props[GreetActor]

}

class GreetActor extends Actor with ActorLogging {

  import GreetActor._
  import akka.pattern.pipe

  private implicit val futureDispatcher = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(20))

  override def receive: Receive = {
    case greet: Greet => {
      logRequest(greet.requestId, "Greet message")
      generateGreet(greet, sender()) pipeTo self
    }
    case GeneratedGreet(requestId, msg, sendTo) => {
      logRequest(requestId, "GeneratedGreetMsg message")
      sendTo ! GreetReply(requestId, msg)
    }
  }

  // assuming that this is a long running operation
  private def generateGreet(greet: Greet, sender: ActorRef) = Future {
    logRequest(greet.requestId, "generateGreet Future")
    GeneratedGreet(greet.requestId, s"Hello ${greet.who} ! How are you? :)", sender)
  }

  private def logRequest(requestId: String, msg: String): Unit = {
    // kamon-akka actor instrumentation propagates mdc :) No need to use withMdc { .. } here
    log.info("thread[{}] reqId[{}] - GreetActor ### {}", Thread.currentThread().getName(), requestId, msg)
  }

}
