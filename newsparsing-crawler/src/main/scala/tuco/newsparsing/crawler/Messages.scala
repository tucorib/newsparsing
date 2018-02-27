package tuco.newsparsing.crawler

sealed trait CrawlerMessage

case object Crawl extends CrawlerMessage
