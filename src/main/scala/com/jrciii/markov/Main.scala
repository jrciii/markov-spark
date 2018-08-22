package com.jrciii.markov

import org.apache.spark.sql.SparkSession

import scala.util.Random

object Main extends App {
  val spark = SparkSession.builder().appName("Markov Generator").getOrCreate()
  val sc = spark.sparkContext
  val files = sc.wholeTextFiles(args(0))
  val tokens = args(1).toInt
  val startWordIndex = args(2).toInt
  val until = args(3).toInt
  def createCombiner(v: String) = Map(v -> 1L)
  def mergeValue(c: Map[String,Long], v: String) = c + (v -> (c.getOrElse(v,0L) + 1L))
  def mergeContainers(a: Map[String,Long], b: Map[String,Long]) = a.foldLeft(b)((acc,x) => {
    acc + (x._1 -> (x._2 + acc.getOrElse(x._1,0L)))
  })
  val chain = files.flatMap(t => {
    val content = t._2
    content
      .split("\\s+")
      //.map(_.toLowerCase.replaceAll("[^0-9a-zA-Z]",""))
      .toStream
      .sliding(tokens+1)
      .filter(_.length != tokens)
      .map(l => (l.init,l.last))
  }).combineByKey(createCombiner,mergeValue,mergeContainers)
    .mapValues(m => {
      val total = m.values.sum.toDouble
      val s = m.mapValues(_ / total).toList.sortBy(_._2)
      // TODO Convert to scalaz foldr
      s.tail.foldLeft(List(s.head))((acc,x) => {
        acc ++ List((x._1,acc.last._2 + x._2))
      })
    }).collect().toMap
  val first = chain.toStream.zipWithIndex.find(_._2 == startWordIndex)
  val rand = new Random()

  def findNext(key: Stream[String]) = for {
    possible <- chain.get(key)
    pSort = possible.sortBy(_._2)
    prob = rand.nextDouble()
    next <- pSort.find(_._2 >= prob).map(_._1)
  } yield next

  def generate(u: Int, key: Stream[String]): Stream[String] = {
    if (u == 0)
      Stream()
    else {
      findNext(key) match {
        case Some(s) => s #:: generate(u-1,key.tail ++ Stream(s))
        case None => Stream()
      }
    }
  }
  for {
    f <- first
    out = f._1._1 ++ generate(until,f._1._1)
    _ = {
      print(out.head)
      out.tail.foreach(s => print(" " + s))
    }
  }
  println("")
}
