package tuco.newsparsing.articles

import java.time.ZonedDateTime
import tuco.newsparsing.data.Article

sealed trait ArticleEvent

case class ArticleCreated(article: Article) extends ArticleEvent
case class ArticleTitleUpdated(title: Option[String], updatedDate: Option[ZonedDateTime]) extends ArticleEvent
case class ArticleTextUpdated(text: Option[String], updatedDate: Option[ZonedDateTime]) extends ArticleEvent
