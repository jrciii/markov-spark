package com.jrciii.markov

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}

import org.apache.hadoop.io.compress.BZip2Codec
import org.apache.spark.sql.SparkSession

object SparkApp extends App {
  val spark = SparkSession.builder().appName("Markov Chain Generator").getOrCreate()
  val sc = spark.sparkContext
  val tokens = args(1).toInt
  val startWordIndex = args(2).toInt
  val until = args(3).toInt
  val out = args(4)
  val parts = args(5).toInt
  val key = LocalDateTime.now().atZone(ZoneId.of("UTC"))
    .format(DateTimeFormatter.ofPattern("yyyy/MM/dd/HHmmss"))

  val files = sc.wholeTextFiles(args(0), parts).map(_._2)

  MarkovChainGenerator
    .generate(files, tokens)
    .saveAsTextFile(out + "/" + key, classOf[BZip2Codec])
}
