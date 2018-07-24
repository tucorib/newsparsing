package tuco.newsparsing.articles

import java.time.ZonedDateTime

import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, Matchers, Suite }

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.testkit.ImplicitSender

trait TestSpec extends Matchers with BeforeAndAfterAll { this: Suite =>
  // Test source id
  val testRssSourceId = "test-rss"
  // Expected article id
  val expectedId = "http://localhost:8080/article.html"
  val expectedPublishedDate = ZonedDateTime.parse("2004-10-19T11:09:11-04:00")
  val expectedUpdatedDate = None
}

class TestKitSpec extends TestKit(ActorSystem("NewsparsingArticles", ConfigFactory.load("test"))) with FlatSpecLike with ImplicitSender with TestSpec {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }
}
