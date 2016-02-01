# akka-http-mdc-logging-kamon proof of concept

I am evaluating akka-http as technology for writing some microservices at work. Being able to propagate requestId and some other parameters in MDC is very important to me. And since MDC doesn't really fit well in asynchronous environment, it can be quite tricky to propagate it in library like akka-http.

This branch has no mdc propagation implemented. It is my starting point for mdc propagation proof of concepts.

# How to run

`sbt run`

`curl -X GET -H "request-id: 321" "http://localhost:8080/greet/kamkor"`

# Example output

```
akka.io.TcpListener mdcReqId[] New connection accepted
me.kamkor.web.GreetEndpoint mdcReqId[] reqId[321] thread[system-akka.actor.default-dispatcher-2] parsed request
me.kamkor.actor.GreetActor mdcReqId[] reqId[321] thread[system-akka.greet-actor-dispatcher-5] Greet message
me.kamkor.actor.GreetActor mdcReqId[] reqId[321] thread[pool-5-thread-5] generateGreet Future
me.kamkor.web.GreetEndpoint mdcReqId[] reqId[321] thread[system-akka.actor.default-dispatcher-2] completing request
me.kamkor.actor.GreetActor mdcReqId[] reqId[321] thread[system-akka.greet-actor-dispatcher-5] GeneratedGreetMsg message
```

Note that GreetEndpoint, GreetActor and future execution in GreetActor use different execution contexts. GreetEndpoint uses default akka dispatcher. GreetActor uses its own akka dispatcher and Future in GreetActor is executed in custom ExecutionContext.