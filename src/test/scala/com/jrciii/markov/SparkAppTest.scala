package com.jrciii.markov

import org.scalatest.{BeforeAndAfterAll, FreeSpec}

class SparkAppTest extends FreeSpec with BeforeAndAfterAll {
  override def beforeAll(): Unit = {
    System.getProperties.put("hadoop.home.dir", "C:\\hadoop")
  }

  "The app should run" in {
    System.setProperty("spark.master","local[*]")
    SparkApp.main(Array("src/test/resources/corpus","2","0","3000","target/markov_chain","4"))
  }
}
