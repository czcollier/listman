package controllers

import play.api._
import play.api.mvc._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json._
import scala.concurrent.Future
import reactivemongo.bson.BSONObjectID

object Application extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("configs")
  def data: JSONCollection = db.collection[JSONCollection]("data")

  import models._
  import JsonCodec._

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def configs(name: String) = Action {
    Async {
      val res = collection
        .find(Json.obj("_id" -> Json.obj("$oid" -> "5182c2cc5977ba5f00365868"))).one[Configuration]

      val comp = res.map(r => r.get.components(name))

      for (c <- comp) {
        data.find(Json.obj("componentId" -> Json.obj("$oid" -> c._id.get)))
      }
      comp
        .map(Json.toJson(_))
        .map { x =>
          Ok(x)
        } recover {
          case e => e.printStackTrace()
          BadRequest(e.getMessage)
        }
    }
  }
}