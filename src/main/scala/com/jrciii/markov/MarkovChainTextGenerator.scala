package com.jrciii.markov

import scala.util.Random

object MarkovChainTextGenerator {
  def generate(chain: Map[List[String], List[(String, Double)]], randGen: Random) = {
    val first: (List[String], List[(String, Double)]) =
      chain.toStream.splitAt(randGen.nextInt(chain.size-1))._2.head
    def findNext(key: List[String]) = for {
      possible <- chain.get(key)
      pSort = possible.sortBy(_._2)
      prob = randGen.nextDouble()
      next <- pSort.find(_._2 > prob).map(_._1)
    } yield next

    def generate(key: List[String]): Stream[String] = {
      findNext(key) match {
        case Some(s) => s #:: generate(key.tail ++ List(s))
        case None => Stream()
      }
    }

    first._1.toStream ++ generate(first._1)
  }
}
