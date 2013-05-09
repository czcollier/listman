package controllers

import play.api.data._
import play.api.data.Forms._
import models.{ComponentInfo, FieldInfo}
import reactivemongo.bson.BSONObjectID

import play.api.data.format.Formats._

object forms {
  val fieldInfoMapping = mapping(
    "id" -> optional(of[String]),
    "name" -> optional(of[String]),
    "dataType" -> optional(of[String]))
    { (id, name, dataType) => FieldInfo(id.map( BSONObjectID(_)), name, dataType) }
    { fi => Some(fi._id.map(_.stringify), fi.name, fi.dataType) }

  val fieldForm = Form(
    fieldInfoMapping
  )

  val componentForm = Form(
    mapping(
      "_id" -> optional(of[String]),
      "name" -> optional(of[String]),
      "fields" -> list(fieldInfoMapping))
    { (id, name, fields) => ComponentInfo(id.map(BSONObjectID(_)), name, fields) }
    { ci => Some(ci._id.map(_.stringify), ci.name, ci.fields) }
  )
}