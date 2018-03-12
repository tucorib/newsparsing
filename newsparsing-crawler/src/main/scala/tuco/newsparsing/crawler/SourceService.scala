package tuco.newsparsing.crawler

import akka.stream.scaladsl.Source
import akka.NotUsed
import akka.actor.Props
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.actor.ActorRef

object SourceService {

  def apply(_system: ActorSystem) = new SourceService(_system)

}

class SourceService(_system: ActorSystem) {

  def crawlSource(sourceId: String, outputRef: ActorRef): Unit = {
    // Extracter ref
    val extracterRef = _system.actorOf(Props(classOf[RssFeedEntryExtracter], outputRef))
    // Get rss downloader ref
    val rssDownloaderActor = _system.actorOf(Props(classOf[RssFeedDownloader], extracterRef))
    // Send message
    rssDownloaderActor ! DownloadRssFeed(sourceId)
  }

}
