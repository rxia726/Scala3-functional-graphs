ThisBuild / scalaVersion := "3.3.6"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "Scala3-functional-graphs",
    libraryDependencies ++= Seq(
      //
      "dev.zio"       %% "zio"      % "2.0.15",
      "dev.zio"       %% "zio-json" % "0.6.0",
      // test
      "org.scalatest" %% "scalatest" % "3.2.16" % Test
    ),
    // Enable the test command to support ScalaTest
    Test / fork := true
  )
