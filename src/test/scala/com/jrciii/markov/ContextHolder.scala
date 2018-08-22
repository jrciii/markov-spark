package com.jrciii.markov

import org.apache.spark.sql.SparkSession

object ContextHolder {
  val session = SparkSession.builder().appName("Markov Tests").master("local[*]").getOrCreate()
}
