package com.jrciii.markov

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FreeSpec, FunSpec}

import scala.util.Random

class MarkovChainTest extends FunSpec with BeforeAndAfterAll {
  var sc: SparkContext = _

  override def afterAll() {
    sc.stop()
  }

  // Yeah I know these aren't great tests :P
  describe("The Markov Tools") {
    System.setProperty("hadoop.home.dir", "C:\\hadoop")
    System.setProperty("spark.master","local[*]")
    System.setProperty("spark.app.name","markov test")
    sc = ContextHolder.session.sparkContext
    val files = sc.wholeTextFiles("src/test/resources/corpus")
    val chain = MarkovChainGenerator.generate(files,2).collect.toMap
    var prob = 0.99
    val text = MarkovChainTextGenerator.generate(chain, new Random() {
      override def nextInt(n: Int): Int = 0

      override def nextDouble() = {
        prob = prob - (1 / 140.0)
        prob
      }
    })
    it("should create the correct chain") {
      println(chain.size)
      println(chain.get(List("the","LORD")))
    }
    it("should create the correct text") {
      val gen = text.take(140)
      assert(gen.size == 140)
      println(gen.mkString(" "))
    }
    sc.stop
  }
}
