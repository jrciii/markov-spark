name := "markov-spark"

version := "0.1"

scalaVersion := "2.11.12"

resolvers += "jitpack" at "https://jitpack.io"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.1"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
// https://mvnrepository.com/artifact/org.scalaz/scalaz-core
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.0-M24"
// https://mvnrepository.com/artifact/com.rometools/rome
libraryDependencies += "com.rometools" % "rome" % "1.11.0"
// https://mvnrepository.com/artifact/org.jsoup/jsoup
libraryDependencies += "com.github.jhy" % "jsoup" % "master-SNAPSHOT"

