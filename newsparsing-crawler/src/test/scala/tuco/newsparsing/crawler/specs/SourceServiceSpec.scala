package tuco.newsparsing.crawler.specs

import akka.stream.{ ActorMaterializer, ActorMaterializerSettings }
import akka.stream.scaladsl.Keep
import akka.stream.testkit.scaladsl.TestSink
import tuco.newsparsing.crawler.{ Article, ExtractRssFeedEntry, MockServer, SourceService, TestKitSpec }
import scala.concurrent.Await
import scala.concurrent.duration._
import org.scalatest.Inspectors

class SourceServiceSpec extends TestKitSpec with Inspectors {

  val mockServer = MockServer(system)

  private implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system))

  override def beforeAll {
    super.beforeAll()
    mockServer.start()
  }

  override def afterAll {
    mockServer.stop()
    super.afterAll()
  }

  behavior of "Source service"

  it must "crawl correct feed entries" in {
    // Get service
    val service = SourceService(system)

    // Crawl
    val seq = Await.result(service.crawl(MockServer.testRssSourceId), 1.seconds)

    forAll (seq) {
      message =>
        {
          // Expect correct sourceId
          assert(message.sourceId == MockServer.testRssSourceId)
          // Expect correct article id
          assert(message.entryUri == MockServer.expectedId)
          // Expect correct article dates
          message.entryPublishedDate should be ('defined)
          assert(MockServer.expectedPublishedDate.isEqual(message.entryPublishedDate.get))
          message.entryUpdatedDate shouldNot be ('defined)
        }
    }
  }

  it must "extract correct data" in {
    // Get service
    val service = SourceService(system)
    // Extract
    val article = Await.result(service.extract(ExtractRssFeedEntry(MockServer.testRssSourceId, MockServer.expectedId, Some(MockServer.expectedPublishedDate), None)), 1.seconds)

    // Expect correct sourceId
    assert(article.sourceId == MockServer.testRssSourceId)
    // Expect correct article id
    assert(article.id == MockServer.expectedId)
    // Expect defined article content
    article.title should be ('defined)
    article.text should be ('defined)
    // Expect correct article dates
    article.publishedDate should be ('defined)
    assert(MockServer.expectedPublishedDate.isEqual(article.publishedDate.get))
    article.updatedDate shouldNot be ('defined)
  }

  it must "stream correct article" in {
    // Get service
    val service = SourceService(system)
    // Get service stream
    val (queue, probe) = service.source.toMat(TestSink.probe[Article])(Keep.both).run()
    // Push message
    queue.offer(MockServer.testRssSourceId)

    val article = probe.requestNext()
    // Expect correct sourceId
    assert(article.sourceId == MockServer.testRssSourceId)
    // Expect correct article id
    assert(article.id == MockServer.expectedId)
    // Expect defined article content
    article.title should be ('defined)
    article.text should be ('defined)
    // Expect correct article dates
    article.publishedDate should be ('defined)
    assert(MockServer.expectedPublishedDate.isEqual(article.publishedDate.get))
    article.updatedDate shouldNot be ('defined)
  }
}
