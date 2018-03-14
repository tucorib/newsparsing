val akkaVersion = "2.5.11"

lazy val commonSettings = Seq(
  organization := "tuco.newsparsing",
  version := "0.1",
  scalaVersion := "2.12.3",
  
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
    "com.rometools" % "rome" % "1.9.0",
    "com.intenthq" %% "gander" % "1.4",
    "com.typesafe.akka" %% "akka-http" % "10.1.0",
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  ),
  parallelExecution in Test := false
)

lazy val data = (project in file("newsparsing-data"))
  .settings(
  	commonSettings,
  	name := "newsparsing-data"
  )

lazy val crawler = (project in file("newsparsing-crawler"))
  .settings(
  	commonSettings,
  	name := "newsparsing-crawler",
  )
  .dependsOn(data)

lazy val articles = (project in file("newsparsing-articles"))
  .settings(
  	commonSettings,
  	name := "newsparsing-articles",
  )
  .dependsOn(data)