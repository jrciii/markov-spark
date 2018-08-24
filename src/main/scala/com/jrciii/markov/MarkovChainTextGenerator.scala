package com.jrciii.markov

import java.io._

import net.openhft.chronicle.map.ChronicleMapBuilder
import org.apache.commons.compress.compressors.CompressorStreamFactory

import scala.util.Random
import scala.collection.JavaConverters._

object MarkovChainTextGenerator {
  def readChain(file: File) = {
    val bs = new BufferedInputStream(new FileInputStream(file))
    val input = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream(bs)))

    val map =
      ChronicleMapBuilder
        .of(classOf[List[String]], classOf[List[(String,Double)]])
        .name("gutenberg-markov-chain")
        .create()
    input.lines().iterator.asScala.foreach{l =>
      val tokens = l.split("\t").toList
      val probs = tokens.tail.map(_.split(" ") match {
        case Array(n,p) => (n,p.toDouble)
      })
      val key = tokens.head.split(" ").toList
      map.put(key,probs)
    }
    map.asScala
  }

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
