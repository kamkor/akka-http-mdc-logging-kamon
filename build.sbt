
lazy val root = (project in file(".")).
  settings(
    organization := "me.kamkor",
    version := "1.0.0",
    scalaVersion := "2.11.7",

    name := "akka-http-mdc-logging-kamon",

//    resolvers ++= Seq(
//      "Kamon Repository Snapshots" at "http://snapshots.kamon.io"
//    ),

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.4" % Test,
      "org.scalacheck" %% "scalacheck" % "1.12.2" % Test
    ),

    libraryDependencies ++= {
      val akkaVersion = "2.4.1"
      val akkaHttpVersion = "2.0.2"
      //val kamonVersion = "0.6.0-a9d5c5c61f7e5e189bf67baee2b13e21ebbaaf73"
      val kamonVersion = "0.5.2"

      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaHttpVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
        "ch.qos.logback" % "logback-classic" % "1.1.3",
        "io.kamon" %% "kamon-core" % kamonVersion,
        "io.kamon" %% "kamon-scala" % kamonVersion,
        "io.kamon" %% "kamon-akka" % kamonVersion
      )


    },

    crossPaths := false,

    //aspectjSettings,

    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

    //javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj

    //fork in run := true
    //javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj
  )
