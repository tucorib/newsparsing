package tuco.newsparsing.crawler

import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpEntity, ContentTypes }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import scala.concurrent.Future
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.Suite
import akka.http.scaladsl.server.HttpApp
import com.typesafe.config.ConfigFactory
import scala.concurrent.Promise
import akka.Done
import scala.concurrent.ExecutionContext
import akka.http.scaladsl.settings.ServerSettings
import akka.event.Logging
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.Source
import akka.actor.ActorSystem

object MockServer {

  // Test source id
  val testRssSourceId = "test-rss"

  // Test HTTP server routes
  def routes: Route = {
    path("test-feed.xml") {
      get {
        val buffer = Source.fromFile("src/test/raw/feed.xml")
        val content = buffer.getLines.mkString
        buffer.close
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, content))
      }
    } ~
      path("article.html") {
        get {
          val buffer = Source.fromFile("src/test/raw/article.html")
          val content = buffer.getLines.mkString
          buffer.close
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, content))
        }
      }
  }

  private implicit val system = ActorSystem("NewsparsingCrawler")
  private implicit val materializer = ActorMaterializer()
  private implicit val ec: ExecutionContext = system.dispatcher

  private var bindingFuture: Future[Http.ServerBinding] = _

  def start() = {

    val conf = ConfigFactory.load("http")
    bindingFuture = Http().bindAndHandle(
      handler   = routes,
      interface = conf.getString("host"),
      port      = conf.getInt("port"),
      settings  = ServerSettings(system))

    Await.ready(bindingFuture, Duration.Inf)
  }

  def stop() = {
    bindingFuture.map(_.unbind()).foreach(_ => system.terminate)
  }
}
