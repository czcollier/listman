package models

import reactivemongo.bson._

case class Configuration(
  _id: Option[BSONObjectID],
  accountId: Option[BSONObjectID],
  name: Option[String],
  components: List[ComponentInfo] = List())

case class ComponentInfo(
  _id: Option[BSONObjectID],
  name: Option[String],
  fields: List[FieldInfo] = List())

case class FieldInfo(
  _id: Option[BSONObjectID],
  name: Option[String],
  label: Option[String],
  dataType: Option[String]) {

  def this() = this(Some(BSONObjectID.generate), Some(""), Some(""), Some(""))
}

case class User(
  _id: Option[BSONObjectID],
  accountIds: List[BSONObjectID],
  username: String,
  password: String
)

case class Account(
  _id: Option[BSONObjectID],
  name: Option[String],
  configurationIds: List[BSONObjectID]
)

case class Raw(
  _id: Option[BSONObjectID],
  componentId: Option[BSONObjectID],
  properties: Map[String, BSONValue] = Map())

case class Res(OK: Boolean, msg: String)

