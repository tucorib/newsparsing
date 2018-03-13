package tuco.newsparsing.crawler

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import org.scalatest.Suite
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.TestKit
import org.scalatest.FlatSpecLike

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
