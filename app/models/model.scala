package models

import reactivemongo.bson._

case class Configuration(
  _id: Option[BSONObjectID],
  name: Option[String],
  components: List[ComponentInfo] = List())

case class ComponentInfo(
  _id: Option[BSONObjectID],
  name: Option[String],
  fields: List[FieldInfo] = List())

case class FieldInfo(
  _id: Option[BSONObjectID],
  name: Option[String],
  dataType: Option[String])

case class User(
  _id: Option[BSONObjectID],
  username: String,
  password: String)

case class Raw(
  _id: Option[BSONObjectID],
  componentId: Option[BSONObjectID],
  properties: Map[String, BSONValue] = Map())

case class Res(OK: Boolean, msg: String)

