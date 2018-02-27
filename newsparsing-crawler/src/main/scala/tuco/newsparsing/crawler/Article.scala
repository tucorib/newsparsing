package tuco.newsparsing.crawler

import java.time.ZonedDateTime

case class Article(id: String, title: String, text: Option[String], publishedDate: Option[ZonedDateTime], updatedDate: Option[ZonedDateTime])
