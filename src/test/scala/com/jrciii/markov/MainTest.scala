package com.jrciii.markov

import java.io.File

import org.scalatest.{BeforeAndAfterAll, FreeSpec}

class MainTest extends FreeSpec with BeforeAndAfterAll {
  override def beforeAll(): Unit = {
    System.getProperties.put("hadoop.home.dir", "C:\\hadoop")
  }

  "The app should run" in {
    System.setProperty("spark.master","local[*]")
    Main.main(Array("src/test/resources","2","0","3000"))
  }
}
