name := "finagle-test2"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-core" % "6.31.0",
  "com.twitter" %% "finagle-http" % "6.31.0",
  "com.twitter" %% "finagle-serversets" % "6.31.0",
  "com.twitter" %% "finagle-zipkin" % "6.31.0",
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"

)
