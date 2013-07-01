logLevel := Level.Warn

// Resolvers
//
resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

// Idea plugins
//
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.4.0")

// Sublime2 + Ensime plugins
//
addSbtPlugin("com.orrsella" % "sbt-sublime" % "1.0.5")

addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.1.1")

// Assembly
//
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.0")

// Dependency graph
//
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.3")

// Revolver
//
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.0")

// Scalariform
//
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.1")

// Update plugin
//
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.1")
