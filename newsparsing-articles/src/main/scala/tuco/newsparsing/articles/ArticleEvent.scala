package tuco.newsparsing.articles

import java.time.ZonedDateTime

import spray.json._
import java.time.format.DateTimeFormatter

sealed trait ArticleEvent

case class ArticleCreated(title: Option[String], text: Option[String], publishedDate: Option[ZonedDateTime], updatedDate: Option[ZonedDateTime]) extends ArticleEvent
case class ArticleTitleUpdated(title: Option[String], updatedDate: Option[ZonedDateTime]) extends ArticleEvent
case class ArticleTextUpdated(text: Option[String], updatedDate: Option[ZonedDateTime]) extends ArticleEvent

object ArticleJsonProtocol extends DefaultJsonProtocol {

  implicit object ZoneDateTimeFormat extends JsonFormat[ZonedDateTime] {

    def write(d: ZonedDateTime) = JsString(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(d))

    def read(v: JsValue) = v match {
      case JsString(d) => ZonedDateTime.parse(d, DateTimeFormatter.ISO_ZONED_DATE_TIME)
      case _ => deserializationError(f"Unable to deserialize $v")
    }
  }

  implicit object ArticleEventJsonFormat extends JsonFormat[ArticleEvent] {

    def write(a: ArticleEvent) = a match {
      case ArticleCreated(title, text, published, updated) => JsObject(
          List(
              Some("type" -> JsString("create")),
              title.map(title => "title" -> JsString(title)),
              text.map(text => "text" -> JsString(text)),
              published.map(published => "published" -> published.toJson),
              updated.map(updated => "updated" -> updated.toJson),
              ).flatten)
      case ArticleTitleUpdated(title, updated) => JsObject(
          List(
              Some("type" -> JsString("title-update")),
              title.map(title => "title" -> JsString(title)),
              updated.map(updated => "updated" -> updated.toJson),
              ).flatten)
       case ArticleTextUpdated(text, updated) => JsObject(
          List(
              Some("type" -> JsString("text-update")),
              text.map(text => "text" -> JsString(text)),
              updated.map(updated => "updated" -> updated.toJson),
              ).flatten)
    }

    def read(value: JsValue) = value match {
      case json@JsObject(fields) =>
        json.getFields("type") match {
          case Seq(JsString("create")) =>
            ArticleCreated(
                    fields.get("title").map(_.convertTo[String]),
                    fields.get("text").map(_.convertTo[String]),
                    fields.get("published").map(_.convertTo[ZonedDateTime]),
                    fields.get("updated").map(_.convertTo[ZonedDateTime])
                )
          case Seq(JsString("title-update")) =>
            ArticleTitleUpdated(
                fields.get("title").map(_.convertTo[String]),
                fields.get("updated").map(_.convertTo[ZonedDateTime])
            )
          case Seq(JsString("text-update")) =>
            ArticleTitleUpdated(
                fields.get("text").map(_.convertTo[String]),
                fields.get("updated").map(_.convertTo[ZonedDateTime])
            )
          case _ => deserializationError(f"")
        }
      case _ => deserializationError(f"")
    }
  }
}
