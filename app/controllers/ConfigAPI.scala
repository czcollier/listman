package controllers

import play.modules.reactivemongo.MongoController
import models.{Configuration, ComponentInfo}
import scala.concurrent.Future
import play.api.mvc.Action
import reactivemongo.bson.BSONObjectID
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.core.commands.Status.ResultMaker

//this is necessary
import JsonCodec._

object ConfigAPI extends JsonController with MongoController with Secured {


  def list = withAuth { sess => implicit request => {
    Async {
      val query = Json.obj("accountId" -> sess.user.accountIds.headOption)
      val res = cfgs.find(query).cursor[Configuration].toList
      jsonSerialize(res)
    }
  }}

  def read(id: String) = withAuth { sess => implicit request => {
    Async {
      val query = Json.obj(
        "accountId" -> sess.user.accountIds.headOption,
        "_id" -> BSONObjectID(id)
      )
      val res = cfgs.find(query).cursor[Configuration].headOption
      jsonSerialize(res)
    }
  }}

  def create = withAuth(parse.json) { sess => implicit req => {
    println("foo1")
    val newCfg = req.body.asOpt[Configuration]
    Async {
      cfgs.save(newCfg).map(result => {
        Ok
      })
    }
  }}

  def update(id: String) = Action(parse.json) { request =>
    Async {
      withConfiguration { cfg =>
        cfgs.save(request.body).map(lastError =>
          Ok("Mongo LastErorr:%s".format(lastError)))
      }
    }
  }

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
