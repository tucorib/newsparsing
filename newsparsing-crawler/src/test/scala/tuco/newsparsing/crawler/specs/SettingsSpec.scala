package tuco.newsparsing.crawler

import org.scalatest.FlatSpec

class SettingsSpec extends FlatSpec with TestSpec {

  behavior of "Settings"

  it must "throw Exception if load method in called more than once" in {
    assertThrows[Exception] {
      Settings.load("test")
    }
  }
}
