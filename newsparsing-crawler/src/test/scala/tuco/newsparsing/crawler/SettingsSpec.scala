package tuco.newsparsing.crawler

class SettingsSpec extends TestSpec {

  behavior of "Settings"

  it must "throw Exception if load method in called more than once" in {
    assertThrows[Exception] {
      Settings.load(settings_test)
    }
  }
}
