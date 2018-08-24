package com.jrciii.markov

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSpec}

class MarkovChainGeneratorTest extends FunSpec with BeforeAndAfterAll {
  describe("The Markov Chain Generator") {
    it("should create the correct chain") {
      System.setProperty("hadoop.home.dir", "C:\\hadoop")
      System.setProperty("spark.master","local[*]")
      System.setProperty("spark.app.name","markov test")
      val sc = SparkSession.builder().getOrCreate().sparkContext
      val files = sc.parallelize(Seq("a b c d e", "a b d e f g", "a b e f h"))
      val chain = MarkovChainGenerator.generate(files,2).collect.toMap
      val expChain = Map(List("b", "c") -> List(("d",1.0)), List("a", "b") -> List(("c",1.0), ("d",0.6666666666666666), ("e",0.3333333333333333)), List("e", "f") -> List(("g",1.0), ("h",0.5)), List("b", "e") -> List(("f",1.0)), List("b", "d") -> List(("e",1.0)), List("c", "d") -> List(("e",1.0)), List("d", "e") -> List(("f",1.0)))
      assertResult(expChain)(chain)
    }
  }
}
