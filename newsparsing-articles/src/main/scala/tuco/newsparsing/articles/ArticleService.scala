package tuco.newsparsing.articles

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object ArticleService {

  def apply(implicit system: ActorSystem) = new ArticleService()

}

class ArticleService(implicit system: ActorSystem) {

  private implicit val materializer = ActorMaterializer()

}
