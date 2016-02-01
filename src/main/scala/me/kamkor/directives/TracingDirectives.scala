package me.kamkor.directives

import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives
import kamon.trace.Tracer

trait TracingDirectives extends Directives {

  def traceContextAwareRoute: server.Directive0 =
    mapInnerRoute { route =>
      ctx => {
        Tracer.withNewContext(traceName = "MDC", autoFinish = true) {
          route(ctx)
        }
      }
    }

}

object TracingDirectives extends TracingDirectives {

}
