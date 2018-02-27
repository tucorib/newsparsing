package tuco.newsparsing.crawler

import org.scalatest.FlatSpec
import org.scalatest.BeforeAndAfterAll

class TestSpec extends FlatSpec with BeforeAndAfterAll {

  val settings_test = "test"

  override def beforeAll() {
    Settings.load(settings_test)
  }

  override def afterAll() {
    Settings.clear()
  }
}
