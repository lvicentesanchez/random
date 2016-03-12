import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

// sbt-docker
import com.typesafe.sbt.packager.docker._

// Resolvers
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)

// Dependencies
val compilerPlugins = Seq(
  compilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
)

val rootDependencies = Seq(
  "io.argonaut"       %% "argonaut"               % "6.1",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2",
  "org.scalaz"        %% "scalaz-core"            % "7.1.0"
)

val testDependencies = Seq (
)

val dependencies =
  compilerPlugins ++
    rootDependencies ++
    testDependencies

// Settings
//
val compileSettings = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:_",
  "-unchecked",
  //"-Xfatal-warnings",
  "-Xlint",
  "-Ybackend:GenBCode",
  "-Ydelambdafy:method",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ywarn-unused-import"
)

val dockerSettings = Seq(
  defaultLinuxInstallLocation in Docker := "/opt/random",
  dockerCommands := Seq(
    Cmd("FROM", "anapsix/alpine-java:jre8"),
    Cmd("ADD", "opt /opt"),
    ExecCmd("RUN", "mkdir", "-p", "/var/log/random"),
    Cmd("EXPOSE", "9000"),
    ExecCmd("ENTRYPOINT", "/opt/random/bin/random")
  ),
  version in Docker := version.value
)

val forkedJvmOption = Seq(
  "-server",
  "-Dfile.encoding=UTF8",
  "-Duser.timezone=GMT",
  "-Xss1m",
  "-Xms2048m",
  "-Xmx2048m",
  "-XX:+CMSClassUnloadingEnabled",
  "-XX:ReservedCodeCacheSize=256m",
  "-XX:+DoEscapeAnalysis",
  "-XX:+UseConcMarkSweepGC",
  "-XX:+UseParNewGC",
  "-XX:+UseCodeCacheFlushing",
  "-XX:+UseCompressedOops"
)

val pluginsSettings =
  dockerSettings ++
  scalariformSettings

val settings = Seq(
  name := "random",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.8",
  libraryDependencies ++= dependencies,
  fork in run := true,
  fork in Test := true,
  fork in testOnly := true,
  connectInput in run := true,
  javaOptions in run ++= forkedJvmOption,
  javaOptions in Test ++= forkedJvmOption,
  scalacOptions := compileSettings,
  mainClass in (Compile, run) := Option("io.github.lvicentesanchez.Boot"),
  ScalariformKeys.preferences := PreferencesImporterExporter.loadPreferences(( file(".") / "formatter.preferences").getPath)
)

val main =
  project
    .in(file("."))
    .settings(
      pluginsSettings ++ settings:_*
    )
    .enablePlugins(JavaAppPackaging)
