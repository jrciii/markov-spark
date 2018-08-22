package com.jrciii.rss

import com.rometools.rome.io._

import scala.collection.JavaConverters._
import java.net.URL

import org.jsoup.Jsoup

object RssScraper {
  def scrape(urls: List[String]) = {
    try {
      val sfi = new SyndFeedInput()
      urls.map(url => {
        val feed = sfi.build(new XmlReader(new URL(url)))

        val entries = feed.getEntries()

        entries.asScala.map(_.getContents.asScala.map(c => {
            Jsoup.parse(c.getValue).text()
        }))
      })
    } catch {
      case e => throw new RuntimeException(e)
    }
  }
}
