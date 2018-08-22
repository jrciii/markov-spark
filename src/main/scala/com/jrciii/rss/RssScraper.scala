package com.jrciii.rss

import com.rometools.rome.io._

import scala.collection.JavaConverters._
import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.examples.HtmlToPlainText

object RssScraper {
  def scrape(urls: List[String]) = {
    try {
      val sfi = new SyndFeedInput()
      val formatter = new HtmlToPlainText
      urls.map(url => {
        val feed = sfi.build(new XmlReader(new URL(url)))

        val entries = feed.getEntries()

        entries.asScala.map(_.getContents.asScala.map(c => {
          c.getValue + "\n===\n" +
            formatter.getPlainText(Jsoup.parse(c.getValue))
        })).mkString("\n\n")
      })
    } catch {
      case e => throw new RuntimeException(e)
    }
  }
}
