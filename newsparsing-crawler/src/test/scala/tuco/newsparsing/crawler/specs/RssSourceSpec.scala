package tuco.newsparsing.crawler

import akka.actor.Props

class RssNewsSourceSpec extends TestKitSpec {

  val mockServer = MockServer(system)

  override def beforeAll {
    super.beforeAll()
    mockServer.start()
  }

  override def afterAll {
    mockServer.stop()
    super.afterAll()
  }

  behavior of "Testing RSS source"

  it must "have a non empty url" in {
    Settings().getSourceRssFeedUrl(MockServer.testRssSourceId) should be ('defined)
  }

  it must "extract correct entry data from feed" in {
    // Get actor ref
    val rssSourceActor = system.actorOf(Props(classOf[RssFeedDownloader], testActor))
    // Tell crawling
    val crawlFuture = rssSourceActor ! DownloadRssFeed(MockServer.testRssSourceId)

    // Expect ExtractRssFeedEntry message back
    val message = expectMsgType[ExtractRssFeedEntry]

    // Expect correct sourceId
    assert(message.sourceId == MockServer.testRssSourceId)
    // Expect correct id
    assert(message.entryUri == MockServer.expectedId)
    // Expect correct dates
    message.entryPublishedDate should be ('defined)
    assert(MockServer.expectedPublishedDate.isEqual(message.entryPublishedDate.get))
    message.entryUpdatedDate shouldNot be ('defined)
  }

  it must "extract correct article" in {
    // Get actor ref
    val rssSourceActor = system.actorOf(Props(classOf[RssFeedEntryExtracter], testActor))
    // Tell extracting
    val crawlFuture = rssSourceActor ! ExtractRssFeedEntry(MockServer.testRssSourceId, MockServer.expectedId, Some(MockServer.expectedPublishedDate), None)

    // Expect ExtractedRssFeedEntry message
    val message = expectMsgType[Article]

    // Expect correct sourceId
    assert(message.sourceId == MockServer.testRssSourceId)
    // Expect correct article id
    assert(message.id == MockServer.expectedId)
    // Expect defined article content
    message.title should be ('defined)
    message.text should be ('defined)
    // Expect correct article dates
    message.publishedDate should be ('defined)
    assert(MockServer.expectedPublishedDate.isEqual(message.publishedDate.get))
    message.updatedDate shouldNot be ('defined)
  }
}
