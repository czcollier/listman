package models

import reactivemongo.bson._

case class Configuration(
  _id: Option[BSONObjectID],
  name: Option[String],
  components: Map[String, ComponentInfo] = Map()) {
}

case class ComponentInfo(
  _id: Option[BSONObjectID],
  fields: Map[String, FieldInfo] = Map())

case class FieldInfo(
  name: Option[String],
  dataType: Option[String])

case class User(
  id: Option[BSONObjectID],
  username: String,
  password: String)

case class Raw(
  id: Option[BSONObjectID],
  componentId: Option[BSONObjectID],
  properties: Map[String, BSONValue] = Map())

case class Res(OK: Boolean, msg: String)

