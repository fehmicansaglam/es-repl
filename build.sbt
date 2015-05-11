name := "es-repl"

organization := "net.fehmicansaglam"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

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
    "org.parboiled" %% "parboiled" % "2.1.0",
    "jline" % "jline" % "2.12",
    "io.searchbox" % "jest" % "0.1.5")
}

shellPrompt in ThisBuild := Common.prompt

import sbtassembly.AssemblyPlugin.defaultShellScript

assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultShellScript))

assemblyJarName in assembly := s"${name.value}-${version.value}"

