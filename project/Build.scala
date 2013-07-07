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
    settings = Defaults.defaultSettings ++ buildSettings ++ compileSettings ++ scalariformSettings ++ Revolver.settings ++ assemblySettings ++ graphSettings) settings (
      resolvers ++= resolverSettings,
      libraryDependencies ++= dependencies,
      ScalariformKeys.preferences := formattingSettings,
      javaOptions in Revolver.reStart ++= forkedJvmOption,
      mainClass in assembly := Option("spray.examples.Boot"),
      excludedJars in assembly <<= (fullClasspath in assembly) map ( _ filter ( _.data.getName == "scala-compiler.jar" ) ),
      jarName in assembly <<= (name, version) map ( (n, v) => "%s-%s.jar".format(n, v) )
    )

  lazy val appName = "shall-be.more"

  lazy val appVersion = "0.1.0"

  lazy val buildSettings = Seq(
    name := appName,
    organization := "more.shall-be",
    version := appVersion,
    scalaVersion := "2.10.2"
  )

  lazy val compileSettings = Seq(
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")
  )

  lazy val dependencies = Seq(
    "com.digital-achiever" %% "brando"          % "0.0.7",
    "com.typesafe.akka"    %% "akka-actor"      % "2.2.0-RC2",
    "com.typesafe.akka"    %% "akka-slf4j"      % "2.2.0-RC2",
    "io.argonaut"          %% "argonaut"        % "6.0-RC3",
    "io.spray"             %  "spray-can"       % "1.2+",
    "io.spray"             %% "spray-json"      % "1.2.5",
    "io.spray"             %  "spray-routing"   % "1.2+",
    "org.scalaz"           %% "scalaz-core"     % "7.0.2",
    "org.scalaz"           %% "scalaz-effect"   % "7.0.2",
    // Test libraries
    "io.spray"             %  "spray-testkit"   % "1.2+" % "test",
    "org.specs2"           %% "specs2"          % "2.0" % "test",
    // Bump dependencies
    "ch.qos.logback"       %  "logback-classic" % "1.0.13",
    "ch.qos.logback"       %  "logback-core"    % "1.0.13",
    "org.slf4j"            %  "slf4j-api"       % "1.7.5"
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
