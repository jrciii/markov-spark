package com.jrciii.markov

import com.jrciii.markov.SparkApp.{chain, until}

import scala.util.Random

object MarkovChainTextGenerator {
  def generate(chain: Map[Stream[String], List[(String, Double)]], randGen: Random) = {
    val first: (Stream[String], List[(String, Double)]) =
      chain.toStream.splitAt(randGen.nextInt(chain.size-1))._2.head
    def findNext(key: Stream[String]) = for {
      possible <- chain.get(key)
      pSort = possible.sortBy(_._2)
      prob = randGen.nextDouble()
      next <- pSort.find(_._2 >= prob).map(_._1)
    } yield next

    def generate(key: Stream[String]): Stream[String] = {

        findNext(key) match {
          case Some(s) => s #:: generate(key.tail ++ Stream(s))
          case None => Stream()
        }

    }

    first._1 ++ generate(first._1)
  }
}
