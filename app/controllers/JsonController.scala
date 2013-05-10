package controllers

import play.api.mvc.Controller
import scala.concurrent.Future
import play.api.libs.json.Writes
import play.api.libs.json.Json
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import models.Configuration
import ControllerHelp._

trait JsonController extends Controller with MongoController {

  import JsonCodec._

  def cfgs: JSONCollection = db.collection[JSONCollection]("configs")

  def jsonSerialize[T](value: Future[T])(implicit tjs : Writes[T]) = {
    value.map(x => Json.prettyPrint(Json.toJson(x)))
      .map(Ok(_))
      .recover {
      case e: BadComponent => NotFound("component type %s not found".format(e.getMessage))
      case e =>  {
        e.printStackTrace()
        BadRequest(e.getClass.getName)
      }
    }
  }

  def withConfiguration[T](op: Configuration => Future[T]) = {
    for {
      cfg <- cfgs.find(byId(stockId)).cursor[Configuration].toList
      ret <- cfg.headOption match {
        case None => Future.failed(BadSession)
        case Some(c) => op(c)
      }
    } yield ret
  }
}
