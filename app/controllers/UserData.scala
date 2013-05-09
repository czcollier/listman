package controllers

import play.api.mvc._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json._
import scala.concurrent.Future
import play.modules.reactivemongo.MongoController
import play.api.mvc.Controller

object UserData extends Controller with MongoController {

  import models._
  import JsonCodec._
  import ControllerHelp._

  private def cfgs: JSONCollection = db.collection[JSONCollection]("configs")
  private def data: JSONCollection = db.collection[JSONCollection]("data")

  private def jsonSerialize[T](value: Future[T])(implicit tjs : Writes[T]) = {
    value.map(x => Json.prettyPrint(Json.toJson(x)))
      .map(Ok(_))
      .recover {
      case e: BadComponent => NotFound("component type %s not found".format(e.getMessage))
      case e =>  BadRequest(e.getClass.getName)
    }
  }

  private def mapComponent(component: ComponentInfo, datum: Raw) = {
    datum.properties.toSeq.map { p =>
      val field = component.fields.find(f => f._id == p._1)
      (field.get.name.get, p._2)
    }.toMap
  }

  def list(name: String) = Action {
    Async {
      val res = for {
        cfg <- cfgs.find(byId(stockId)).cursor[Configuration].toList
        results <- {
          cfg.headOption match {
            case None => Future.failed(BadSession)
            case Some(q) => q.components.find(x => x.name == name) match {
              case None => Future.failed(BadComponent(name))
              case Some(c) => {
                val rawData = data.find(byId(c._id.get.stringify, "componentId")).cursor[Raw].toList
                for (rd <- rawData) yield rd.map(d => mapComponent(c, d))
              }
            }
          }
        }
      } yield results

      jsonSerialize(res)
    }
  }
}
