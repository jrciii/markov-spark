package com.jrciii.markov

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}

import awscala._
import awscala.s3._
import org.apache.spark.sql.SparkSession

import scala.util.Random

object SparkApp extends App {
  implicit val s3 = S3.at(Region.US_EAST_1)
  val spark = SparkSession.builder().appName("Markov Generator").getOrCreate()
  val sc = spark.sparkContext
  val tokens = args(1).toInt
  val startWordIndex = args(2).toInt
  val until = args(3).toInt
  val bucket = args(4)
  val parts = args(5).toInt
  val key = LocalDateTime.now().atZone(ZoneId.of("UTC"))
    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd/hh:mm:ss"))

  val files = sc.wholeTextFiles(args(0), parts)

  val chain: Map[List[String], List[(String, Double)]] =
    MarkovChainGenerator.generate(files, tokens).collect().toMap

  spark.stop()
  sc.stop()

  val words = MarkovChainTextGenerator.generate(chain, new Random()).take(until)
  val text = words.mkString(" ")

  println(text)
  println("")
  println(s3.buckets)
  s3.putObject(bucket,key + "_" + words.size,text)
}
