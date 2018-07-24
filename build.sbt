val akkaVersion = "2.5.11"

lazy val commonSettings = Seq(
  organization := "tuco.newsparsing",
  version := "0.1",
  scalaVersion := "2.12.3",
  
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  ),
  parallelExecution in Test := false,
  
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
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
  	libraryDependencies ++= Seq(
  	  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
      "com.rometools" % "rome" % "1.9.0",
      "com.intenthq" %% "gander" % "1.4",
      "com.typesafe.akka" %% "akka-http" % "10.1.0",
  	)
  )
  .dependsOn(data)

lazy val articles = (project in file("newsparsing-articles"))
  .settings(
  	commonSettings,
  	name := "newsparsing-articles",
  	libraryDependencies ++= Seq(
  	  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  	  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  	  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  	  "io.spray" %%  "spray-json" % "1.3.3",
  	  "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.1.1" % "test",
  	  "com.safety-data" %% "akka-persistence-redis" % "0.4.0",
  	),
  	fork := true
  )
  .dependsOn(data)