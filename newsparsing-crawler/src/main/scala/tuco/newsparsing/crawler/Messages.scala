package tuco.newsparsing.crawler

import java.net.URL
import com.rometools.rome.feed.synd.SyndEntry
import java.time.ZonedDateTime
import akka.actor.ActorRef

sealed trait CrawlerMessage

case class DownloadRssFeed(sourceId: String) extends CrawlerMessage
case class ExtractRssFeedEntry(sourceId: String, entryUri: String, entryPublishedDate: Option[ZonedDateTime], entryUpdatedDate: Option[ZonedDateTime]) extends CrawlerMessage
case class Article(sourceId: String, id: String, title: Option[String], text: Option[String], publishedDate: Option[ZonedDateTime], updatedDate: Option[ZonedDateTime])
