package com.jrciii.markov

import java.io.File
import java.util.ResourceBundle

import org.scalatest.{BeforeAndAfterAll, FreeSpec}

import scala.util.Properties

class SparkAppTest extends FreeSpec with BeforeAndAfterAll {
  override def beforeAll(): Unit = {
    System.getProperties.put("hadoop.home.dir", "C:\\hadoop")
  }

  "The app should run" in {
    val props = ResourceBundle.getBundle("SparkApp")
    System.setProperty("spark.master","local[*]")
    val bucket = props.getString("markov.spark.bucket")
    SparkApp.main(Array("src/test/resources/corpus","2","0","3000",bucket,"4"))
  }
}
