package tuco.newsparsing.crawler

import org.scalatest.{ Suites, BeforeAndAfterAll, Matchers, FlatSpec, FlatSpecLike }
import akka.testkit.{ TestKit, ImplicitSender }
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.Suite

trait TestSpec extends Matchers with BeforeAndAfterAll { this: Suite =>

  override def beforeAll {
    Settings.load("test")
  }

  override def afterAll {
    Settings.clear()
  }
}

class TestKitSpec extends TestKit(ActorSystem("NewsparsingCrawler", ConfigFactory.load("test"))) with FlatSpecLike with TestSpec {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }
}
