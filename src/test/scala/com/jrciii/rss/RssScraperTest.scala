package com.jrciii.rss

import org.scalatest.FreeSpec

class RssScraperTest extends FreeSpec {
  "Run dat boi on /r/all" in {
    println(RssScraper.scrape(List("https://www.reddit.com/r/all/comments.rss")))
  }
}
