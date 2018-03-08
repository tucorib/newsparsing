package tuco.newsparsing.crawler

import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.ExecutionContext
import java.time.ZonedDateTime
import scala.concurrent.duration._
import akka.testkit.TestProbe
import com.typesafe.config.ConfigFactory

class RssNewsSourceSpec extends TestKitSpec(ActorSystem("NewsparsingCrawlerSystem", ConfigFactory.load("test"))) {

  override def beforeAll {
    // super call
    super.beforeAll()
    // Start mock server
    MockServer.start()
  }

  override def afterAll {
    // Stop mock server
    MockServer.stop()
    // super call
    super.afterAll()
  }

  behavior of "Testing RSS source"

  it must "have a non empty url" in {
    Settings().getSourceRssFeedUrl(MockServer.testRssSourceId) should be ('defined)
  }

  it must "extract correct entry data from feed" in {
    // Test probe
    val probe = TestProbe()
    // Get actor ref
    val rssSourceActor = system.actorOf(Props(classOf[RssFeedDownloader], probe.ref))
    // Tell crawling
    val crawlFuture = rssSourceActor ! DownloadRssFeed(MockServer.testRssSourceId)

    // Expect ExtractRssFeedEntry message back
    val message = probe.expectMsgType[ExtractRssFeedEntry]

    // Expect correct sourceId
    assert(message.sourceId == MockServer.testRssSourceId)
    // Expect correct id
    assert(message.entryUri == "http://localhost:8080/article.html")
    // Expect correct dates
    message.entryPublishedDate should be ('defined)
    assert(ZonedDateTime.parse("2004-10-19T11:09:11-04:00").isEqual(message.entryPublishedDate.get))
    message.entryUpdatedDate shouldNot be ('defined)
  }

  it must "extract correct article" in {
    // Test probe
    val probe = TestProbe()
    // Get actor ref
    val rssSourceActor = system.actorOf(Props(classOf[RssFeedEntryExtracter], probe.ref))
    // Tell extracting
    val crawlFuture = rssSourceActor ! ExtractRssFeedEntry(MockServer.testRssSourceId, "http://localhost:8080/article.html", Some(ZonedDateTime.parse("2004-10-19T11:09:11-04:00")), None)

    // Expect ExtractedRssFeedEntry message
    val message = probe.expectMsgType[ExtractedRssFeedEntry]

    // Expect correct sourceId
    assert(message.sourceId == MockServer.testRssSourceId)
    // Expect corrrect article id
    assert(message.article.id == "http://localhost:8080/article.html")
    // Expect corrrect article dates
    message.article.publishedDate should be ('defined)
    assert(ZonedDateTime.parse("2004-10-19T11:09:11-04:00").isEqual(message.article.publishedDate.get))
    message.article.updatedDate shouldNot be ('defined)
  }
}
