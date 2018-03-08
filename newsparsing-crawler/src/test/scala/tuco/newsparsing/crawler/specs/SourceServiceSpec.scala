package tuco.newsparsing.crawler.specs

import tuco.newsparsing.crawler.TestKitSpec
import tuco.newsparsing.crawler.SourceService
import tuco.newsparsing.crawler.Article
import tuco.newsparsing.crawler.MockServer

class SourceServiceSpec extends TestKitSpec {

  val mockServer = MockServer(system)
  val service = SourceService(system)

  override def beforeAll {
    super.beforeAll()
    mockServer.start()
  }

  override def afterAll {
    mockServer.stop()
    super.afterAll()
  }

  behavior of "Source service"

  it must "extract correct article" in {
    // Execute service
    service.crawlSource(MockServer.testRssSourceId, testActor)

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
