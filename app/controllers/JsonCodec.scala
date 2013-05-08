package controllers

import models._
import reactivemongo.bson._
import play.api.libs.json._
import play.api.libs.json.Json.JsValueWrapper

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
  implicit val objectMapFormat = new Format[Map[String, BSONValue]] {
    def writes(map: Map[String, BSONValue]): JsValue =
      Json.obj(map.map{ case (s, o) =>
        val ret:(String, JsValueWrapper) = o match {
          case v:BSONString => s -> JsString(v.value)
          case v:BSONNumberLike => s -> JsNumber(BigDecimal(v.toDouble))
          case v:BSONArray => s -> JsArray(v)
        }
        ret
      }.toSeq:_*)


    def reads(jv: JsValue): JsResult[Map[String, Object]] =
      JsSuccess(jv.as[Map[String, JsValue]].map{case (k, v) =>
        k -> (v match {
          case s:JsString => s.as[String]
          case l => l.as[List[String]]
        })
      })
  }

  class RawValueFormat[T] extends Format[T] {
    def reads(json: JsValue) = (json \ )
    def writes(o: T) = ???
  }
  implicit val fieldFormat = Json.format[FieldInfo]
  implicit val componentInfoFormat = Json.format[ComponentInfo]
  implicit val configurationFormat = Json.format[Configuration]
  implicit val rawFormat = Json.format[Raw]
}
