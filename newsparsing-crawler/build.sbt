name := "newsparsing-crawler"
version := "0.1"
scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "com.rometools" % "rome" % "1.9.0",
  "com.intenthq" %% "gander" % "1.4",
  "com.typesafe.akka" %% "akka-actor" % "2.5.9",
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
