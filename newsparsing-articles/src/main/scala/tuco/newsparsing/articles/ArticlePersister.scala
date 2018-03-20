package tuco.newsparsing.articles

import akka.persistence.PersistentActor
import tuco.newsparsing.data.Article
import scala.collection.immutable.Seq

class ArticlePersister(sourceId: String, articleId: String) extends PersistentActor {

  override def persistenceId = s"$sourceId-$articleId"

  var state: Option[Article] = None

  private def onReceiveArticle(article: Article): Seq[ArticleEvent] =
    state match {
      case Some(article_state) =>
        (if (article_state.title != article.title) Seq(ArticleTitleUpdated(article.title, article.updatedDate)) else Seq.empty) ++
          (if (article_state.text != article.text) Seq(ArticleTextUpdated(article.text, article.updatedDate)) else Seq.empty)
      case None => Seq(ArticleCreated(article))
    }

  val receiveRecover: Receive = {
    case articleEvent: ArticleEvent => updateState(articleEvent)
  }

  val receiveCommand: Receive = {
    case article: Article =>
      persistAll(onReceiveArticle(article)) { articleEvent => updateState(articleEvent) }
      sender ! new ArticleSaved(article.sourceId, article.id)
  }

  private def updateState(articleEvent: ArticleEvent) =
    articleEvent match {
      case ArticleCreated(article)                 => state = Some(article)
      case ArticleTitleUpdated(title, updatedDate) => state = state.map(_.copy(title = title, updatedDate = updatedDate))
      case ArticleTextUpdated(text, updatedDate)   => state = state.map(_.copy(text = text, updatedDate = updatedDate))
    }
}
