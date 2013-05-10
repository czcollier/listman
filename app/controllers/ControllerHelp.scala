package controllers

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID

object ControllerHelp {
  val stockId ="518bd34bde91ce4723cc1b77" //"5182c2cc5977ba5f00365868"

  case class BadComponent(n: String) extends RuntimeException {
    override def getMessage = n
  }
  case object BadSession extends RuntimeException
  case object BadUser extends RuntimeException

  def byId(idVal: String, fieldName: String = "_id") =
    Json.obj(fieldName -> Json.obj("$oid" -> idVal))

  implicit def objectId2String(id: Option[BSONObjectID]) = id.get.stringify
}
