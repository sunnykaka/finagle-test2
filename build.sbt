name := "finagle-test2"

version := "1.0"

scalaVersion := "2.11.7"

val finagleVersion = "6.33.0"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-core" % finagleVersion,
  "com.twitter" %% "finagle-http" % finagleVersion,
  "com.twitter" %% "finagle-serversets" % finagleVersion,
  "com.twitter" %% "finagle-zipkin" % finagleVersion,
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
