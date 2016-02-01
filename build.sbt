
lazy val root = (project in file(".")).
  settings(
    organization := "me.kamkor",
    version := "1.0.0",
    scalaVersion := "2.11.7",

    name := "akka-http-mdc-logging-kamon",

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.4" % Test,
      "org.scalacheck" %% "scalacheck" % "1.12.2" % Test
    ),

    libraryDependencies ++= {
      val akkaVersion = "2.4.1"
      val akkaHttpVersion = "2.0.2"

      Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "com.typesafe.akka" %% "akka-stream-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-core-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-xml-experimental" % akkaHttpVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
        "ch.qos.logback" % "logback-classic" % "1.1.3"
      )

    },

    crossPaths := false,

    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

  )
