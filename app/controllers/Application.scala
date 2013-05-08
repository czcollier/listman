package controllers

import play.api._
import play.api.mvc._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.json._
import scala.concurrent.Future
import play.api.libs.iteratee.Enumeratee
import reactivemongo.bson.BSONObjectID

object Application extends Controller with MongoController {

  case class BadComponent(n: String) extends RuntimeException {
    override def getMessage = n
  }
  case object BadSession extends RuntimeException

  def cfgs: JSONCollection = db.collection[JSONCollection]("configs")
  def data: JSONCollection = db.collection[JSONCollection]("data")

  import models._

  private def jsonSerialize[T](value: Future[T])(implicit tjs : Writes[T]) = {
    value.map(Json.toJson(_))
      .map(Ok(_))
      .recover {
        case e: BadComponent => NotFound("")
        case e =>  BadRequest(e.getClass.getName)
      }
  }

  private def byId(idVal: String, fieldName: String = "_id") =
    Json.obj(fieldName -> Json.obj("$oid" -> idVal))

  import JsonCodec._

  val stockId = "5182c2cc5977ba5f00365868"


  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def configs(name: String) = Action {
    Async {
      val res = for {
        cfg <- cfgs.find(byId(stockId)).cursor[Configuration].toList
        results <- {
          cfg.headOption match {
            case None => Future.failed(BadSession)
            case Some(q) => q.components.get(name) match {
              case None => Future.failed(BadComponent(name))
              case Some(c) => data.find(byId(c._id.get.stringify, "componentId"))
                  .cursor[JsObject].toList
          }
        }
      }} yield results

      jsonSerialize(res)
    }
  }
}

