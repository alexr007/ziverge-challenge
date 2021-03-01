Global / onChangedBuildSource := ReloadOnSourceChanges

name    := "ziverge-challenge"
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.5"
scalacOptions ++= Seq(
  "-deprecation",
  "-Xfatal-warnings",
  "-Wunused",
)

libraryDependencies ++= Seq(
  "com.lihaoyi"       %% "upickle"           % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-typed"  % "2.6.13",
  "com.typesafe.akka" %% "akka-stream"       % "2.6.13",
  "com.typesafe.akka" %% "akka-http"         % "10.2.4",
  "ch.qos.logback"    %  "logback-classic"   % "1.2.3",
)

mainClass in (Compile, run) := Some("collector.AppStreamingAndHttp")
