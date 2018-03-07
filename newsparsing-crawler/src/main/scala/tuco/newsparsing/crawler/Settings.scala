package tuco.newsparsing.crawler

import com.typesafe.config._

class Settings private (config: Config) {

	private def getSourceRssConfig(sourceId: String): Option[Config] = {
		if (config.hasPath(s"sources.$sourceId.type")) {
			config.getString(s"sources.$sourceId.type") match {
				case "rss" => Some(config.getConfig(s"sources.$sourceId"))
				case _ => None
			}
		} else
			None
	}

	def getSourceRssFeedUrl(sourceId: String): Option[String] = this.getSourceRssConfig(sourceId).map(config => config.getString("url"))
}

object Settings {

	private var cache = Option.empty[Settings]

	def apply(): Settings =
		cache.getOrElse(throw new Exception)

	def load(name: String): Unit =
		cache match {
			case Some(s) => throw new Exception
			case None =>
				val s = new Settings(ConfigFactory.load(name).getConfig("newsparsing-crawler"))
				cache = Some(s)
		}

	def clear(): Unit =
		cache = None
}
