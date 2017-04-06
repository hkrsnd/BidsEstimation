scalaVersion := "2.11.5"

val scalazVersion = "7.1.0"

libraryDependencies ++= Seq(
  "net.ruippeixotog" %% "scala-scraper" % "1.0.0",
  "org.jsoup" % "jsoup" % "1.7.3"
)

//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

/*
resolvers ++= Seq(
  // other resolvers here
  // if you want to use snapshot builds (currently 0.12-SNAPSHOT), use this.
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)*/

//scalacOptions += "-feature"

//initialCommands in console := "import scalaz._, Scalaz._"
//initialCommands in console := "import SVM._"
