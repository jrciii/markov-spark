package com.jrciii.markov

import org.apache.spark.sql.SparkSession

import scala.util.Random
import awscala._, s3._

object SparkApp extends App {
  implicit val s3 = S3.at(Region.US_EAST_1)
  val spark = SparkSession.builder().appName("Markov Generator").getOrCreate()
  val sc = spark.sparkContext
  val files = sc.wholeTextFiles(args(0))
  val tokens = args(1).toInt
  val startWordIndex = args(2).toInt
  val until = args(3).toInt
  val bucket = args(4)
  val key = java.time.Clock.systemUTC().instant().formatted("yyyy-MM-ddThh:mm:ssZ")

  val chain: Map[Stream[String], List[(String, Double)]] =
    MarkovChainGenerator.generateMarkovChain(files, tokens).collect().toMap

  spark.stop()
  sc.stop()

  val text = MarkovChainTextGenerator.generate(chain, new Random()).take(until).mkString(" ")
  println(text)
  println("")
}
