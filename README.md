# akka-http-mdc-logging-kamon proof of concept

I am evaluating akka-http as technology for writing some microservices at work. Being able to propagate requestId and some other parameters in MDC is very important to me. And since MDC doesn't really fit well in asynchronous environment, it can be quite tricky to propagate it in library like akka-http.

In this repository I am trying to propagate requestId in MDC with the help of kamon.io. I have very little experience with akka-http and kamon.io. This repository is just my proof of concept for my evaluation of akka-http. I will try to improve the solution, and if I can't then I will try something else.  

Cons of current solution:

* have to manually propagate mdc using withMdc { .. } when logging with normal (non akka) logger 
* it is easy to lose trace context in route directives. It has to be set again with Trace.withContext(context) { .. }
* not publicly documented features of kamon.io
* requires AspectJ Weaver agent
* there's lots of magic in this solution
* i didn't test this solution properly yet

# How to run

`sbt aspectj-runner:run`

`curl -X GET -H "request-id: 127" "http://localhost:8080/greet/kamkor"`

# Example output

```
mdcReqId[123] thread[system-akka.actor.default-dispatcher-6] reqId[123] - GreetEndpoint ### parsed request
mdcReqId[123] thread[system-akka.greet-actor-dispatcher-7] reqId[123] - GreetActor ### Greet message
mdcReqId[123] thread[pool-5-thread-13] reqId[123] - GreetActor ### generateGreet Future
mdcReqId[123] thread[system-akka.actor.default-dispatcher-2] reqId[123] - GreetEndpoint ### completing request
mdcReqId[123] thread[system-akka.greet-actor-dispatcher-7] reqId[123] - GreetActor ### GeneratedGreetMsg message
```

Note that GreetEndpoint, GreetActor and future execution in GreetActor use different execution contexts. GreetEndpoint uses default akka dispatcher. GreetActor uses its own akka dispatcher and Future in GreetActor is executed in custom ExecutionContext.