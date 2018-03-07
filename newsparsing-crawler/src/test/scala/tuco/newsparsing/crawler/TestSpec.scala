package tuco.newsparsing.crawler

import akka.testkit.TestKit
import org.scalatest.FlatSpec
import akka.actor.ActorSystem
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import akka.testkit.ImplicitSender
import org.scalatest.FlatSpecLike

trait TestSpec extends Matchers with BeforeAndAfterAll { this: Suite =>

  val settings_test = "test"

  override def beforeAll {
    super.beforeAll()
    Settings.load(settings_test)
  }

  override def afterAll {
    Settings.clear()
    super.afterAll()
  }

}
class TestFlatSpec extends FlatSpec with TestSpec
class TestKitSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with FlatSpecLike with TestSpec {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    super.afterAll
  }
}
