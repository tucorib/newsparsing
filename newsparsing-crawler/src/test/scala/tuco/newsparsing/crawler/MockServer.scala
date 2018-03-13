package tuco.newsparsing.crawler

import java.time.ZonedDateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.io.Source

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._enhanceRouteWithConcatenation
import akka.http.scaladsl.server.Directives._segmentStringToPathMatcher
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.get
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.settings.ServerSettings
import akka.stream.ActorMaterializer

object MockServer {

  // Test source id
  val testRssSourceId = "test-rss"

  // Expected article id
  val expectedId = "http://localhost:8080/article.html"
  val expectedPublishedDate = ZonedDateTime.parse("2004-10-19T11:09:11-04:00")
  val expectedUpdatedDate = None

  def apply(implicit system: ActorSystem) = new MockServer()

}

class MockServer(implicit system: ActorSystem) {

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
    bindingFuture.map(_.unbind())
  }
}
