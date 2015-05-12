name := "es-repl"

organization := "net.fehmicansaglam"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-encoding", "utf8",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials")

libraryDependencies ++= Seq(
  "org.parboiled" %% "parboiled" % "2.1.0",
  "jline" % "jline" % "2.12",
  "io.searchbox" % "jest" % "0.1.5",
  "org.slf4j" % "slf4j-simple" % "1.7.2"
)

shellPrompt in ThisBuild := Common.prompt

import sbtassembly.AssemblyPlugin.defaultShellScript

assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript))

assemblyJarName in assembly := s"${name.value}"

