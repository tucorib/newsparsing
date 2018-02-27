package tuco.newsparsing.crawler

import org.scalatest.Matchers

class RssSourceSpec extends TestSpec with Matchers {

  val testRssSource = "test-rss"

  behavior of "Testing RSS source"

  it must "have a non empty url" in {
    Settings().getSourceRssFeedUrl(testRssSource) should be('defined)
  }
}
