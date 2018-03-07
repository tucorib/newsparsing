package tuco.newsparsing.crawler

import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.ExecutionContext
import java.time.ZonedDateTime
import scala.concurrent.duration._

class RssNewsSourceSpec extends TestKitSpec(ActorSystem("RssNewsSourceSpec")) {

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
    // Get actor ref
    val rssSourceActor = system.actorOf(Props(classOf[RssNewsSource], MockServer.testRssSourceId))
    // Tell crawling
    val crawlFuture = rssSourceActor ! Crawl
    // Expect only one message back
    val messages = expectMsgType[Seq[Article]]
    // Expect no more message
    expectNoMessage(2.seconds)
    // Check message
    messages.foreach {
      // Article expected
      case Article(id, title, text, publishedDate, updatedDate) =>
        // Expect correct id
        assert(id == "http://localhost:8080/article.html")
        // Expect content
        title shouldNot be ('empty)
        text shouldNot be ('empty)
        // Expect correct dates
        publishedDate should be ('defined)
        assert(ZonedDateTime.parse("2004-10-19T11:09:11-04:00").isEqual(publishedDate.get))
        updatedDate shouldNot be ('defined)
      // Error if not Article
      case _ => assert(false)
    }
  }
}
