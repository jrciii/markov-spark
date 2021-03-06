name := "markov-spark"

version := "0.1"

scalaVersion := "2.11.12"

Compile/mainClass := Some("com.jrciii.markov.Main")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

resolvers += "jitpack" at "https://jitpack.io"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.1"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
// https://mvnrepository.com/artifact/com.rometools/rome
libraryDependencies += "com.rometools" % "rome" % "1.11.0"
// https://mvnrepository.com/artifact/org.jsoup/jsoup
libraryDependencies += "com.github.jhy" % "jsoup" % "master-SNAPSHOT"
