package tuco.newsparsing.articles

import akka.actor.ActorSystem
import tuco.newsparsing.data.Article

object ArticleService {

  def apply(implicit system: ActorSystem) = new ArticleService()

}

class ArticleService(implicit system: ActorSystem) {

  def storeArticle(article: Article) = ???
}
