package tuco.newsparsing.data

import java.time.ZonedDateTime

case class Article(sourceId: String, id: String, title: Option[String], text: Option[String], publishedDate: Option[ZonedDateTime], updatedDate: Option[ZonedDateTime])
