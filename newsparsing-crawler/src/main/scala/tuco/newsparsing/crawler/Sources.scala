package tuco.newsparsing.crawler

import akka.actor.Actor
import akka.event.Logging
import java.net.URL
import com.rometools.rome.feed.synd.{ SyndFeed }
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import scala.collection.JavaConverters._
import com.intenthq.gander.Gander
import com.google.common.io.Resources
import java.nio.charset.Charset
import akka.actor.ActorSystem
import akka.actor.Props
import com.intenthq.gander.PageInfo
import java.time.ZonedDateTime
import java.time.ZoneId

abstract class NewsSource(sourceId: String) extends Actor {

  def receive = {
    case Crawl => sender ! crawl
  }

  def crawl(): Seq[Article]

}

class RssNewsSource(sourceId: String) extends NewsSource(sourceId: String) {

  val configUrl = Settings().getSourceRssFeedUrl(sourceId)
  val input = new SyndFeedInput

  def crawl(): Seq[Article] = {
    configUrl match {
      case Some(url) =>
        // Get feed URL
        val feedUrl = new URL(url)
        // Get feed content
        val feed: SyndFeed = input.build(new XmlReader(feedUrl))
        // Get feed entries as scala seq
        val entries = feed.getEntries.asScala
        // Iterate on RSS entries
        for {
          entry <- entries
          // Get raw HTML from entry URL
          rawHTML = Resources.toString(new URL(entry.getUri), Charset.forName("UTF8"))
          // Extract informations from raw HTML
          pageInfo <- Gander.extract(rawHTML)

          // Id
          id = entry.getUri
          // Title
          title = pageInfo.processedTitle
          // Text
          text = pageInfo.cleanedText
          // Published date
          publishedDate = Option(entry.getPublishedDate).flatMap(pd => Some(ZonedDateTime.ofInstant(pd.toInstant, ZoneId.of("UTC"))))
          // Updated date
          updatedDate = Option(entry.getUpdatedDate).flatMap(ud => Some(ZonedDateTime.ofInstant(ud.toInstant, ZoneId.of("UTC"))))
        } yield Article(id, title, text, publishedDate, updatedDate)
      case None => Seq[Article]()
    }
  }
}
