package controllers

import models._
import reactivemongo.bson.BSONObjectID
import play.api.libs.json._

object JsonCodec {
  import play.api.libs.json.Json
  import play.modules.reactivemongo.json.BSONFormats._

//  implicit val objectIdFormat = Format[BSONObjectID](
//    (__ \ "$oid").read[String].map { s =>
//      BSONObjectID(s)
//    },
//
//    Writes[BSONObjectID] {
//      s => JsString(s.stringify)
//    }
//  )

  implicit val fieldFormat = Json.format[FieldInfo]
  implicit val componentInfoFormat = Json.format[ComponentInfo]
  implicit val configurationFormat = Json.format[Configuration]
  //implicit val rawFormat = Json.format[Raw]
}
