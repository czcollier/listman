package controllers

import play.modules.reactivemongo.MongoController
import models.{Configuration, ComponentInfo}
import scala.concurrent.Future
import reactivemongo.bson.BSONObjectID
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats._ //this is necessary
import libs.JsonManip._

import JsonCodec._

object ConfigAPI extends JsonController with MongoController with Secured {

  def list = withAuth { sess => implicit request => {
    Async {
      val query = Json.obj("accountId" -> sess.user.accountIds.headOption)
      val res = cfgs.find(query).cursor[Configuration].toList
      jsonSerialize(res)
    }
  }}

  private def getAs[T](id: String, acctId: Option[BSONObjectID])(implicit reads: play.api.libs.json.Reads[T]) = {
    val query = Json.obj(
      "accountId" -> acctId,
      "_id" -> BSONObjectID(id)
    )

    cfgs.find(query).cursor[T].headOption
  }

  def read(id: String) = withAuth { sess => implicit request => {
    Async {
      jsonSerialize(getAs[Configuration](id, sess.defaultAccountId))
    }
  }}

  def create = withAuth(parse.json) { sess => implicit req => {
    val newCfg = req.body.as[Configuration]
    if (newCfg._id.isDefined) {
      BadRequest("new objects can not have IDs pre-assigned")
    }
    else {
      val cfgWithAccount = newCfg.copy(accountId = sess.defaultAccountId)
      Async {
        cfgs.save(cfgWithAccount).map { _=>  Ok }
      }
    }
  }}

  def update(id: String) = withAuth(parse.json) { sess => implicit req => {
    val newCfg = req.body.as[JsObject]
    val oldCfg = getAs[JsObject](id, sess.defaultAccountId)

    Async {
      oldCfg.flatMap { oc => {
        val merged = oc.get.deepMerge2(newCfg).as[Configuration]
        val withAccount = merged.copy(accountId = sess.defaultAccountId)
        cfgs.save(withAccount).map { _ => Ok }
      }}
    }
  }}

  def component(id: String) = withAuth { username => implicit request =>
    Async {
      val res = withConfiguration { cfg =>
        cfg.components.find(c => c._id.get.stringify == id) match {
          case None => Future(ComponentInfo(None, None, List()))
          case Some(c) =>  Future(c)
        }
      }

      jsonSerialize(res)
    }
  }
}
