package com.jrciii.markov

import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}

import org.apache.spark.sql.SparkSession

import scala.util.Random
import awscala._
import org.joda.time.DateTimeZone
import s3._

object SparkApp extends App {
  implicit val s3 = S3.at(Region.US_EAST_1)
  val spark = SparkSession.builder().appName("Markov Generator").getOrCreate()
  val sc = spark.sparkContext
  val files = sc.wholeTextFiles(args(0))
  val tokens = args(1).toInt
  val startWordIndex = args(2).toInt
  val until = args(3).toInt
  val bucket = args(4)
  val key = LocalDateTime.now().atZone(ZoneId.of("UTC"))
    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd/hh:mm:ss"))

  val chain: Map[Stream[String], List[(String, Double)]] =
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
