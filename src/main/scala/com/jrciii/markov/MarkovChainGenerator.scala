package com.jrciii.markov

import org.apache.spark.rdd.RDD

object MarkovChainGenerator {
  val wsRegex = "\\s+".r
  val toStrip = "[^a-zA-Z0-9,.?!@$:;'&$#&\\-_+=/|\\\\^~`]".r

  def generate(files: RDD[String], tokens: Int) = {
    def createCombiner(v: String) = Map(v -> 1L)

    def mergeValue(c: Map[String, Long], v: String) = c + (v -> (c.getOrElse(v, 0L) + 1L))

    def mergeContainers(a: Map[String, Long], b: Map[String, Long]) = a.foldLeft(b)((acc, x) => {
      acc + (x._1 -> (x._2 + acc.getOrElse(x._1, 0L)))
    })

    files.flatMap(content => {
      wsRegex.split(content)
        .map(s => toStrip.replaceAllIn(s,""))
        .toList
        .sliding(tokens + 1)
        .filter(_.length == tokens + 1)
        .map(l => (l.init.mkString(" "), l.last))
    }).combineByKey(createCombiner, mergeValue, mergeContainers)
      .map(m => {
        val total = m._2.values.sum.toDouble
        val s = m._2.mapValues(_ / total)
        m._1 + "\t" + s.tail.foldLeft(List(s.head))((acc,x) => {
          (x._1, x._2 + acc.head._2) :: acc
        }).map(t => t._1 + " " + t._2).mkString("\t")
      })
  }
}