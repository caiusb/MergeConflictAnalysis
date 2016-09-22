import sbt._

scalaVersion := "2.11.7"

EclipseKeys.withSource := true

addCommandAlias("idea", "update-classifiers; update-sbt-classifiers; gen-idea sbt-classifiers")

libraryDependencies ++= Seq(
  "org.eclipse.jgit" % "org.eclipse.jgit" % "4.0.2.201509141540-r",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.10.0",
  "args4j" % "args4j" % "2.33",
  "net.sf.trove4j" % "trove4j" % "3.0.3",
  "org.apache.maven.shared" % "maven-invoker" % "2.2",
  // for Gumtree Client
  "com.nanohttpd" % "nanohttpd-webserver" % "2.1.1",
  "org.rendersnake" % "rendersnake" % "1.8",
  "com.brindescu" %% "conflict-detector" % "0.1"
)

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

resolvers += Resolver.sonatypeRepo("public")

resolvers += "Mine" at "http://releases.ivy.brindescu.com"

val mc = Some("edu.oregonstate.mergeproblem.mergeconflictanalysis.Main")

mainClass in (Compile, run) := mc

mainClass in assembly := mc

lazy val root = (project in file(".")).
  settings(
    name := "MergeConflictAnalysis",
    version := "1.2"
  )

lazy val versionReport = TaskKey[String]("version-report")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
