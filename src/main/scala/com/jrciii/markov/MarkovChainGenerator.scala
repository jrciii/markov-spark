package com.jrciii.markov

import org.apache.spark.rdd.RDD

object MarkovChainGenerator {
  val wsRegex = "\\s+".r
  val toStrip = "[^a-zA-Z0-9,.?!@$:;'&$#&-_+=/|\\\\^~`]".r

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
        .filter(_.length != tokens)
        .map(l => (l.init, l.last))
    }).combineByKey(createCombiner, mergeValue, mergeContainers)
      .mapValues(m => {
        val total = m.values.sum.toDouble
        val s = m.mapValues(_ / total)
        s.tail.foldLeft(List(s.head))((acc,x) => {
          (x._1, x._2 + acc.head._2) :: acc
        })
      })
  }
}