package tuco.newsparsing.crawler

import java.time.ZonedDateTime

case class Article(id: String, title: Option[String], text: Option[String], publishedDate: Option[ZonedDateTime], updatedDate: Option[ZonedDateTime])
