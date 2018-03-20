package tuco.newsparsing.articles

import akka.serialization._
import spray.json._
import ArticleJsonProtocol._
import scala.io.Codec

class ArticleEventSerializer extends SerializerWithStringManifest {

  val ArticleEventManifest = "article-event"

  def identifier: Int = 1414

  def manifest(o: AnyRef): String =
    o match {
      case _: ArticleEvent => ArticleEventManifest
      case _               => throw new Exception(f"Unsupported type ${o.getClass}")
    }

  def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    manifest match {
      case ArticleEventManifest => ???
      case _                    => throw new Exception(f"Unsupported manifest $manifest")
    }

  def toBinary(o: AnyRef): Array[Byte] =
    o match {
      case e: ArticleEvent => e.toJson.compactPrint.getBytes(Codec.UTF8.charSet)
      case _               => throw new Exception(f"Unsupported type ${o.getClass}")
    }

}
