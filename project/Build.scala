import sbt._
import Keys._
// sbt-scalariform
import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._
// sbt-assembly
import sbtassembly.Plugin._
import AssemblyKeys._
// sbt-revolver
import spray.revolver.RevolverPlugin._
// sbt-dependecy-graph
import net.virtualvoid.sbt.graph.Plugin._

object RootBuild extends Build {
  lazy val main = Project(
    id = "main",
    base = file("."),
    settings = Defaults.coreDefaultSettings ++ buildSettings ++ compileSettings ++ scalariformSettings ++ Revolver.settings ++ assemblySettings ++ graphSettings) settings (
      resolvers ++= resolverSettings,
      libraryDependencies ++= dependencies,
      ScalariformKeys.preferences := formattingSettings,
      fork in run := true,
      connectInput in run := true,
      javaOptions in run ++= forkedJvmOption,
      mainClass in assembly := Option("io.github.lvicentesanchez.Boot"),
      excludedJars in assembly <<= (fullClasspath in assembly) map ( _ filter ( _.data.getName == "scala-compiler.jar" ) ),
      jarName in assembly <<= (name, version) map ( (n, v) => "%s-%s.jar".format(n, v) )
    )

  lazy val appName = "shall-be.more"

  lazy val appVersion = "0.1.0"

  lazy val buildSettings = Seq(
    name := appName,
    organization := "io.github.lvicentesancheze",
    version := appVersion,
    scalaVersion := "2.11.7"
  )

  lazy val compileSettings = Seq(
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")//, "-Xlog-implicits")
  )

  lazy val dependencies = Seq(
    "io.argonaut"       %% "argonaut"               % "6.1",
    "com.typesafe.akka" %% "akka-http-experimental" % "2.0-M1",
    "org.scalaz"        %% "scalaz-core"            % "7.1.0"
  )


  lazy val forkedJvmOption = Seq(
    "-server",
    "-Dfile.encoding=UTF8",
    "-Xss1m",
    "-Xms1536m",
    "-Xmx1536m",
    "-XX:+CMSClassUnloadingEnabled",
    "-XX:MaxPermSize=384m",
    "-XX:ReservedCodeCacheSize=256m",
    "-XX:+DoEscapeAnalysis",
    "-XX:+UseConcMarkSweepGC",
    "-XX:+UseParNewGC",
    "-XX:+UseCodeCacheFlushing",
    "-XX:+UseCompressedOops"
  )

  lazy val formattingSettings =
    FormattingPreferences()
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, false)
      .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 40)
      .setPreference(CompactControlReadability, false)
      .setPreference(CompactStringConcatenation, false)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(FormatXml, true)
      .setPreference(IndentLocalDefs, false)
      .setPreference(IndentPackageBlocks, true)
      .setPreference(IndentSpaces, 2)
      .setPreference(IndentWithTabs, false)
      .setPreference(MultilineScaladocCommentsStartOnFirstLine, false)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, false)
      .setPreference(PreserveSpaceBeforeArguments, false)
      .setPreference(PreserveDanglingCloseParenthesis, true)
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(SpaceBeforeColon, false)
      .setPreference(SpaceInsideBrackets, false)
      .setPreference(SpaceInsideParentheses, false)
      .setPreference(SpacesWithinPatternBinders, true)

  lazy val resolverSettings = Seq(
    "chris dinn repository" at "http://chrisdinn.github.com/releases/",
    "sonatype oss releases" at "http://oss.sonatype.org/content/repositories/releases/",
    "sonatype oss snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    "spray nightlies" at "http://nightlies.spray.io/",
    "spray releases" at "http://repo.spray.io",
    "typesafe releases" at "http://repo.typesafe.com/typesafe/releases/",
    "typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  )
}
