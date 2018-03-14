package tuco.newsparsing.crawler

import java.time.ZonedDateTime

case class ExtractRssFeedEntry(sourceId: String, entryUri: String, entryPublishedDate: Option[ZonedDateTime], entryUpdatedDate: Option[ZonedDateTime])
