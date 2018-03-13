package tuco.newsparsing.crawler

import java.io.StringReader
import java.time.{ ZoneId, ZonedDateTime }

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.immutable.Iterable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.intenthq.gander.Gander
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedInput

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ ActorMaterializer, OverflowStrategy }
import akka.stream.scaladsl.Source

object SourceService {

  def apply(implicit system: ActorSystem) = new SourceService()

}

class SourceService(implicit system: ActorSystem) {

  private val input = new SyndFeedInput
  private val http = Http(system)

  private implicit val materializer = ActorMaterializer()

  val source = Source.queue[String](Settings().getStreamSourceBufferSize, OverflowStrategy.backpressure)
    .mapAsyncUnordered(Settings().getStreamSourceCrawlParallelism)(crawl)
    .mapConcat(s => Iterable(s: _*))
    .mapAsyncUnordered(Settings().getStreamSourceExtractParallelism)(extract)

  protected[crawler] def crawl(sourceId: String) = {
    Settings().getSourceRssFeedUrl(sourceId) match {
      case Some(feedUri) =>
        http.singleRequest(HttpRequest(uri = feedUri)) // Request feed URI
          .flatMap(httpResponse => Unmarshal(httpResponse.entity).to[String]) // Get request content
          .map {
            rawHTML =>
              // Get feed content
              val feed: SyndFeed = input.build(new StringReader(rawHTML))
              // Get feed entries as scala seq
              val entries = feed.getEntries.asScala
              // Iterate on RSS entries
              entries.map { entry =>
                // Published date
                val entryPublishedDate = Option(entry.getPublishedDate).flatMap(pd => Some(ZonedDateTime.ofInstant(pd.toInstant, ZoneId.of("UTC"))))
                // Updated date
                val entryUpdatedDate = Option(entry.getUpdatedDate).flatMap(ud => Some(ZonedDateTime.ofInstant(ud.toInstant, ZoneId.of("UTC"))))

                // Return message
                ExtractRssFeedEntry(sourceId, entry.getUri, entryPublishedDate, entryUpdatedDate)
              }
          }
      case None => Future.successful(Seq.empty)
    }
  }

  protected[crawler] def extract(message: ExtractRssFeedEntry): Future[Article] = {
    http.singleRequest(HttpRequest(uri = message.entryUri))
      .flatMap(httpResponse => Unmarshal(httpResponse.entity).to[String])
      .map { rawHTML =>
        // Extract informations from raw HTML
        val pageInfo = Gander.extract(rawHTML)

        // Id
        val id = message.entryUri
        // Title
        val title = pageInfo.flatMap(pi => Some(pi.processedTitle))
        // Text
        val text = pageInfo.flatMap(_.cleanedText)
        // Published date
        val publishedDate = message.entryPublishedDate
        // Updated date
        val updatedDate = message.entryUpdatedDate

        // Transfer article
        Article(message.sourceId, id, title, text, publishedDate, updatedDate)
      }
  }
}
