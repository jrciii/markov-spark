package com.jrciii.rss

import org.scalatest.FreeSpec

class RssScraperTest extends FreeSpec {
  "Run dat boi on /r/all" in {
    RssScraper.scrape(List("https://www.reddit.com/r/all/comments.rss"))
      .foreach(_.foreach(_.foreach(l => println(l + "\n"))))
  }
}
