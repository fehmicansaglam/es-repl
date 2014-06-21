name := "elasticsearch-repl"

organization := "net.fehmicansaglam"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding", "utf8",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials")

libraryDependencies <++= scalaVersion { scalaVersion =>
  Seq(
    "org.parboiled" %% "parboiled" % "2.0.0",
    "org.scala-lang" % "scala-reflect" % "2.11.1",
    "com.sksamuel.elastic4s" %% "elastic4s" % "1.2.1.1" intransitive(),
    "org.elasticsearch" % "elasticsearch" % "1.2.1",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.4.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.1",
    "jline" % "jline" % "2.12")
}

parallelExecution in Test := false

Keys.fork := false

testOptions in Test ++= Seq(
  Tests.Argument("showtimes", "true"),
  Tests.Argument("junitxml", "console"))

assemblySettings

scalariformSettings

shellPrompt in ThisBuild := Common.prompt
