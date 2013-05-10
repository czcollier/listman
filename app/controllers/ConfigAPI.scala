package controllers

import play.modules.reactivemongo.MongoController
import models.{Configuration, ComponentInfo}
import scala.concurrent.Future
import play.api.mvc.Action
import reactivemongo.bson.BSONDocument
import play.api.libs.json.{JsObject, Json}

object ConfigAPI extends JsonController with MongoController with Secured {

  import JsonCodec._

  def list = Action {

    val query = BSONDocument("$query" -> BSONDocument())

    Async {
      val res = cfgs.find(Json.obj()).cursor[Configuration].toList
      jsonSerialize(res)
    }
  }

  def read = Action {
    Async {
      withConfiguration { cfg =>
        jsonSerialize(Future(cfg))
      }
    }
  }

  def create = Action(parse.json) { request =>
    Async {
      cfgs.insert(request.body).map(lastError =>
        Ok("Mongo LastErorr:%s".format(lastError)))
    }
  }

  def update(id: String) = Action(parse.json) { request =>
    Async {
      withConfiguration { cfg =>
        val cfg = request.body.as[Configuration]

        cfgs.insert(request.body).map(lastError =>
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
