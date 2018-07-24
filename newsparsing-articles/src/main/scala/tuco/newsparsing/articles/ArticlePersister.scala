package tuco.newsparsing.articles

import akka.persistence.PersistentActor
import tuco.newsparsing.data.Article
import scala.collection.immutable.Seq

case object GetState

class ArticlePersister(sourceId: String, articleId: String) extends PersistentActor {

  override def persistenceId = s"$sourceId-$articleId"

  var state: Option[Article] = None

  private def onReceiveArticle(article: Article): Seq[ArticleEvent] =
    if (sourceId == article.sourceId && articleId == article.id)
      state match {
        case Some(article_state) =>
          (if (article_state.title != article.title) Seq(ArticleTitleUpdated(article.title, article.updatedDate)) else Seq.empty) ++
            (if (article_state.text != article.text) Seq(ArticleTextUpdated(article.text, article.updatedDate)) else Seq.empty)
        case None => Seq(ArticleCreated(article.title, article.text, article.publishedDate, article.updatedDate))
      }
    else
      throw new Exception(f"Wrong persister used for source ${article.sourceId}, article ${article.id}")

  val receiveRecover: Receive = {
    case articleEvent: ArticleEvent => updateState(articleEvent)
  }

  val receiveCommand: Receive = {
    case GetState =>
      sender ! state
    case article: Article =>
      persistAll(onReceiveArticle(article)) { articleEvent => updateState(articleEvent) }
      sender ! new ArticleSaved(article.sourceId, article.id)
  }

  private def updateState(articleEvent: ArticleEvent) =
    articleEvent match {
      case ArticleCreated(title, text, publishedDate, updatedDate) => state = Some(Article(sourceId, articleId, title, text, publishedDate, updatedDate))
      case ArticleTitleUpdated(title, updatedDate) => state = state.map(_.copy(title = title, updatedDate = updatedDate))
      case ArticleTextUpdated(text, updatedDate) => state = state.map(_.copy(text = text, updatedDate = updatedDate))
    }
}
