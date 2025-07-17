import sbt._
import Keys._

ThisBuild / scalaVersion := "3.3.6"
ThisBuild / version      := "0.1.0-SNAPSHOT"

// Module core : contient Graph, GraphOperations, JsonCodecs, GraphViz et dépendances ScalaTest
lazy val core = (project in file("."))
  .settings(
    name := "Scala3-functional-graphs-core",
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"        % "2.0.15",
      "dev.zio"       %% "zio-json"   % "0.6.0",
      "org.scalatest" %% "scalatest"  % "3.2.16" % Test
    ),
    // Exécuter les tests dans un sous-processus pour éviter les conflits
    Test / fork := true
  )

// Module app : application terminale basée sur ZIO 2 + tests ZIO
lazy val app = (project in file("app"))
  .dependsOn(core)
  .settings(
    name := "Scala3-functional-graphs-app",
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"          % "2.0.15",
      "dev.zio"       %% "zio-test"     % "2.0.15" % Test,
      "dev.zio"       %% "zio-test-sbt" % "2.0.15" % Test
    ),
    // Permet à sbt de reconnaître ZIO Test comme framework de test
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    Test / fork := true
  )

// Projet racine : agrège core et app, mais n'est pas empaqueté lui-même
lazy val root = (project in file("."))
  .aggregate(core, app)
  .settings(
    publish / skip := true,
    name := "Scala3-functional-graphs"
  )
