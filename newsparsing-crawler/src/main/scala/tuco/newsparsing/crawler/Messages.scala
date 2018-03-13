package tuco.newsparsing.crawler

import java.time.ZonedDateTime

sealed trait CrawlerMessage

case class ExtractRssFeedEntry(sourceId: String, entryUri: String, entryPublishedDate: Option[ZonedDateTime], entryUpdatedDate: Option[ZonedDateTime]) extends CrawlerMessage
case class Article(sourceId: String, id: String, title: Option[String], text: Option[String], publishedDate: Option[ZonedDateTime], updatedDate: Option[ZonedDateTime])
