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

  it must "receive correct article when crawling" in {
    // Test probe
    val probe = TestProbe()
    // Get actor ref
    val rssSourceActor = system.actorOf(Props(classOf[RssFeedDownloader], probe.ref))
    // Tell crawling
    val crawlFuture = rssSourceActor ! DownloadRssFeed(MockServer.testRssSourceId)

    // Expect only one message back
    val message = probe.expectMsgType[ExtractRssFeedEntry]
    // Expect no more message
    probe.expectNoMessage(2.seconds)

    // Expect correct id
    assert(message.entryUri == "http://localhost:8080/article.html")
    // Expect correct dates
    message.entryPublishedDate should be ('defined)
    assert(ZonedDateTime.parse("2004-10-19T11:09:11-04:00").isEqual(message.entryPublishedDate.get))
    message.entryUpdatedDate shouldNot be ('defined)
  }
}
