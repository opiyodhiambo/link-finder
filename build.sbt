name := "link-finder"

version := "1.0"

scalaVersion := "3.3.0"

lazy val akkaVersion = "2.8.4"


// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion cross CrossVersion.for3Use2_13,
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test cross CrossVersion.for3Use2_13,
  "org.scalatest" %% "scalatest" % "3.1.4" % Test cross CrossVersion.for3Use2_13,
  "com.ning" %% "async-http-client" % "1.9.4"
)
