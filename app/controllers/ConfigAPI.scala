package controllers

import play.modules.reactivemongo.MongoController
import models.{Configuration, ComponentInfo}
import scala.concurrent.Future
import play.api.mvc.Action
import reactivemongo.bson.BSONObjectID
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats._ //this is necessary

import JsonCodec._

object ConfigAPI extends JsonController with MongoController with Secured {


  def list = withAuth { sess => implicit request => {
    Async {
      val query = Json.obj("accountId" -> sess.user.accountIds.headOption)
      val res = cfgs.find(query).cursor[Configuration].toList
      jsonSerialize(res)
    }
  }}

  private def get(id: String, acctId: Option[BSONObjectID]) = {
    val query = Json.obj(
      "accountId" -> acctId,
      "_id" -> BSONObjectID(id)
    )

    cfgs.find(query).cursor[Configuration].headOption
  }

  def read(id: String) = withAuth { sess => implicit request => {
    Async {
      jsonSerialize(get(id, sess.defaultAccountId))
    }
  }}

  def addAccountId(id: Option[BSONObjectID]) = (__).json.update {
    __.read[JsObject].map {
      o => o ++ Json.obj("accountId" -> id)
    }
  }

  def createWithTransform = withAuth { sess => implicit request => {
    val jsonBody = request.body.asJson.get.transform(addAccountId(sess.user.accountIds.headOption)).asOpt.get
    println(jsonBody)
    Async {
      cfgs.save(jsonBody).map { _ => Ok }
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
    Async {
      for {
        newCfg <- Future(req.body.as[Configuration])
        oldCfg <- get(id, sess.defaultAccountId)
      } yield Ok((oldCfg.toString, newCfg.toString).toString)
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
