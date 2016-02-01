# akka-http-mdc-logging-kamon proof of concept

I am evaluating akka-http as technology for writing some microservices at work. Being able to propagate requestId and some other parameters in MDC is very important to me. And since MDC doesn't really fit well in asynchronous environment, it can be quite tricky to propagate it in library like akka-http.

In this repository I am trying to propagate requestId in MDC with the help of kamon.io. I have very little experience with akka-http and kamon.io. This repository is just my proof of concept for my evaluation of akka-http. I will try to improve the solution, and if I can't then I will try something else.  

Cons of current solution:

* have to manually propagate mdc using withMdc { .. } when logging with normal (non akka) logger 
* not publicly documented features of kamon.io https://github.com/kamon-io/Kamon/issues/272 http://kamon.io/integrations/logback/mdc-in-an-asyncronous-environment/
* requires AspectJ Weaver agent
* there's lots of magic in this solution
* i didn't test this solution properly yet

# How to run

`sbt aspectj-runner:run`

`curl -X GET -H "request-id: 321" "http://localhost:8080/greet/kamkor"`

`/batchcurl.sh 20` where 20 is number of curls to execute

# Example output

```
akka.io.TcpListener mdcReqId[] New connection accepted
me.kamkor.web.GreetEndpoint mdcReqId[321] reqId[321] thread[system-akka.actor.default-dispatcher-3] parsed request
me.kamkor.actor.GreetActor mdcReqId[321] reqId[321] thread[system-akka.greet-actor-dispatcher-6] Greet message
me.kamkor.actor.GreetActor mdcReqId[321] reqId[321] thread[pool-5-thread-5] generateGreet Future
me.kamkor.web.GreetEndpoint mdcReqId[321] reqId[321] thread[system-akka.actor.default-dispatcher-4] completing request
me.kamkor.actor.GreetActor mdcReqId[321] reqId[321] thread[system-akka.greet-actor-dispatcher-6] GeneratedGreetMsg message
```

Note that GreetEndpoint, GreetActor and future execution in GreetActor use different execution contexts. GreetEndpoint uses default akka dispatcher. GreetActor uses its own akka dispatcher and Future in GreetActor is executed in custom ExecutionContext.
