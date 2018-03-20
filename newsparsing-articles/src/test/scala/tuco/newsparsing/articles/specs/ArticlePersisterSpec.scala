package tuco.newsparsing.articles.specs

import org.scalatest.Inspectors
import tuco.newsparsing.articles.TestKitSpec
import akka.actor.Props
import tuco.newsparsing.articles.ArticlePersister
import tuco.newsparsing.data.Article
import scala.concurrent.duration._
import tuco.newsparsing.articles.ArticleSaved

class ArticlePersisterSpec extends TestKitSpec with Inspectors {

  behavior of "ArticlePersister"

  it must "ignore article that are not dedicated" in {
    val actorRef = system.actorOf(Props(classOf[ArticlePersister], testRssSourceId, expectedId))

    // Wrong source
    actorRef ! new Article(testRssSourceId, s"$expectedId-test", Some("Title1"), Some("Text1"), Some(expectedPublishedDate), expectedUpdatedDate)
    expectNoMessage(2.seconds)
    // Wrong article
    actorRef ! new Article(s"$testRssSourceId-test", expectedId, Some("Title1"), Some("Text1"), Some(expectedPublishedDate), expectedUpdatedDate)
    expectNoMessage(2.seconds)
  }

  it must "send back a single ArticleSaved message after each update or creation" in {
    val actorRef = system.actorOf(Props(classOf[ArticlePersister], testRssSourceId, expectedId))

    // Creation
    actorRef ! new Article(testRssSourceId, expectedId, Some("Title1"), Some("Text1"), Some(expectedPublishedDate), expectedUpdatedDate)
    expectMsg[ArticleSaved](new ArticleSaved(testRssSourceId, expectedId))

    // Single update
    actorRef ! new Article(testRssSourceId, expectedId, Some("Title1"), Some("Text2"), Some(expectedPublishedDate), expectedUpdatedDate)
    expectMsg[ArticleSaved](new ArticleSaved(testRssSourceId, expectedId))

    // Multiple updates
    actorRef ! new Article(testRssSourceId, expectedId, Some("Title2"), Some("Text3"), Some(expectedPublishedDate), expectedUpdatedDate)
    expectMsg[ArticleSaved](new ArticleSaved(testRssSourceId, expectedId))
  }
}
