enablePlugins(JavaAppPackaging)
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.16"


lazy val root = (project in file("."))
  .settings(
    name := "zio-notifications",
    //scalacOptions ++= Seq("-java-output-version", "11"),

      libraryDependencies ++= Seq(
          "dev.zio" %% "zio" % "2.1.18",
          "dev.zio" %% "zio-streams" % "2.1.18",
          "dev.zio" %% "zio-test" % "2.1.10",
          "dev.zio" %% "zio-test-sbt" % "2.1.10" % "test",
          "dev.zio" %% "zio-http" % "3.0.1",
          "io.getquill" %% "quill-jdbc-zio" % "4.8.5",
          "dev.zio" %% "zio-json" % "0.6.2",
          "dev.zio" %% "zio-kafka" % "2.8.2",
          "com.sendgrid" % "sendgrid-java" % "4.10.3",
          "com.twilio.sdk" % "twilio" % "10.1.0",
          "ch.qos.logback" % "logback-classic" % "1.4.14"
      )

  )
