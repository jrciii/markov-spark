package org.jsoup.examples

import org.jsoup.Jsoup
import org.jsoup.internal.StringUtil
import org.jsoup.helper.Validate
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor
import java.io.IOException


/**
  * HTML to plain-text. This example program demonstrates the use of jsoup to convert HTML input to lightly-formatted
  * plain-text. That is divergent from the general goal of jsoup's .text() methods, which is to get clean data from a
  * scrape.
  * <p>
  * Note that this is a fairly simplistic formatter -- for real world use you'll want to embrace and extend.
  * </p>
  * <p>
  * To invoke from the command line, assuming you've downloaded the jsoup jar to your current directory:</p>
  * <p><code>java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]</code></p>
  * where <i>url</i> is the URL to fetch, and <i>selector</i> is an optional CSS selector.
  *
  * @author Jonathan Hedley, jonathan@hedley.net
  */
object HtmlToPlainText {
  private val userAgent = "Mozilla/5.0 (jsoup)"
  private val timeout = 5 * 1000

  @throws[IOException]
  def main(args: String*): Unit = {
    Validate.isTrue(args.length == 1 || args.length == 2, "usage: java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]")
    val url = args(0)
    val selector = if (args.length == 2) args(1)
    else null
    // fetch the specified URL and parse to a HTML DOM
    val doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get
    val formatter = new HtmlToPlainText
    if (selector != null) {
      val elements = doc.select(selector) // get each element that matches the CSS selector
      import scala.collection.JavaConversions._
      for (element <- elements) {
        val plainText = formatter.getPlainText(element) // format that element to plain text
        System.out.println(plainText)
      }
    }
    else { // format the whole doc
      val plainText = formatter.getPlainText(doc)
      System.out.println(plainText)
    }
  }
}

class HtmlToPlainText {
  /**
    * Format an Element to plain-text
    *
    * @param element the root element to format
    * @return formatted text
    */
  def getPlainText(element: Element): String = {
    val formatter = new FormattingVisitor
    NodeTraversor.traverse(formatter, element) // walk the DOM, and call .head() and .tail() for each node

    formatter.toString
  }

  // the formatting rules, implemented in a breadth-first DOM traverse
  private object FormattingVisitor {
    private val maxWidth = 80
  }

  private class FormattingVisitor extends NodeVisitor {
    private var width = 0
    private val accum = new StringBuilder // holds the accumulated text

    // hit when the node is first seen
    override def head(node: Node, depth: Int): Unit = {
      val name = node.nodeName
      if (node.isInstanceOf[TextNode]) append(node.asInstanceOf[TextNode].text) // TextNodes carry all user-readable text in the DOM.
      else if (name == "li") append("\n * ")
      else if (name == "dt") append("  ")
      else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr")) append("\n")
    }

    // hit when all of the node's children (if any) have been visited
    override def tail(node: Node, depth: Int): Unit = {
      val name = node.nodeName
      if (StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")) append("\n")
      else if (name == "a") append(String.format(" <%s>", node.absUrl("href")))
    }

    // appends text to the string builder with a simple word wrap method
    private def append(text: String): Unit = {
      if (text.startsWith("\n")) width = 0 // reset counter if starts with a newline. only from formats above, not in natural text
      if (text == " " && (accum.length == 0 || StringUtil.in(accum.substring(accum.length - 1), " ", "\n"))) return // don't accumulate long runs of empty spaces
      if (text.length + width > FormattingVisitor.maxWidth) { // won't fit, needs to wrap
        val words = text.split("\\s+")
        var i = 0
        while ( {
          i < words.length
        }) {
          var word = words(i)
          val last = i == words.length - 1
          if (!last) { // insert a space if not the last word
            word = word + " "
          }
          if (word.length + width > FormattingVisitor.maxWidth) { // wrap and reset counter
            accum.append("\n").append(word)
            width = word.length
          }
          else {
            accum.append(word)
            width += word.length
          }

          {
            i += 1; i - 1
          }
        }
      }
      else { // fits as is, without need to wrap text
        accum.append(text)
        width += text.length
      }
    }

    override def toString: String = accum.toString
  }

}