package tuco.newsparsing.crawler

import akka.actor.Actor
import akka.event.Logging
import java.net.URL
import com.rometools.rome.feed.synd.{ SyndFeed }
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.XmlReader
import scala.collection.JavaConverters._
import com.intenthq.gander.Gander
import akka.http.scaladsl.Http
import akka.actor.ActorSystem
import akka.actor.Props
import com.intenthq.gander.PageInfo
import java.time.ZonedDateTime
import java.time.ZoneId
import scala.concurrent.Future
import scala.util.{ Failure, Success }
import akka.http.scaladsl.model._
import java.io.InputStream
import java.io.StringReader
import akka.util.ByteString
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.actor.ActorRef
import akka.actor.ActorLogging

class RssFeedDownloader(entryExtracter: ActorRef) extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  val http = Http(context.system)
  val input = new SyndFeedInput

  private final implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  def receive = {
    case DownloadRssFeed(sourceId) =>
      Settings().getSourceRssFeedUrl(sourceId).foreach { feedUri =>
        http.singleRequest(HttpRequest(uri = feedUri)) // Request feed URI
          .flatMap(httpResponse => Unmarshal(httpResponse.entity).to[String]) // Get request content
          .onComplete {
            case Success(rawHTML) =>
              // Get feed content
              val feed: SyndFeed = input.build(new StringReader(rawHTML))
              // Get feed entries as scala seq
              val entries = feed.getEntries.asScala
              // Iterate on RSS entries
              entries.foreach { entry =>
                // Published date
                val entryPublishedDate = Option(entry.getPublishedDate).flatMap(pd => Some(ZonedDateTime.ofInstant(pd.toInstant, ZoneId.of("UTC"))))
                // Updated date
                val entryUpdatedDate = Option(entry.getUpdatedDate).flatMap(ud => Some(ZonedDateTime.ofInstant(ud.toInstant, ZoneId.of("UTC"))))
                // Transfer
                entryExtracter ! ExtractRssFeedEntry(sourceId, entry.getUri, entryPublishedDate, entryUpdatedDate)
              }
            case Failure(_) => sender ! _
          }
      }
  }
}

class RssFeedEntryExtracter(articleHandler: ActorRef) extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  val http = Http(context.system)

  private final implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  def receive = {
    case ExtractRssFeedEntry(sourceId, entryUri, entryPublishedDate, entryUpdatedDate) =>
      http.singleRequest(HttpRequest(uri = entryUri))
        .flatMap(httpResponse => Unmarshal(httpResponse.entity).to[String])
        .onComplete {
          case Success(rawHTML) =>
            // Extract informations from raw HTML
            val pageInfo = Gander.extract(rawHTML)

            // Id
            val id = entryUri
            // Title
            val title = pageInfo.flatMap(pi => Some(pi.processedTitle))
            // Text
            val text = pageInfo.flatMap(_.cleanedText)
            // Published date
            val publishedDate = entryPublishedDate
            // Updated date
            val updatedDate = entryUpdatedDate

            // Transfer article
            articleHandler ! Article(sourceId, id, title, text, publishedDate, updatedDate)
          case Failure(_) => sender ! _
        }
  }

}
