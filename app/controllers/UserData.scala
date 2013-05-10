package controllers

import play.api.mvc._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json._
import scala.concurrent.Future
import play.modules.reactivemongo.MongoController
import play.api.mvc.Controller

object UserData extends JsonController with MongoController {

  import models._
  import JsonCodec._
  import ControllerHelp._

  private def data: JSONCollection = db.collection[JSONCollection]("data")

  private def mapComponent(component: ComponentInfo, datum: Raw) = {
    datum.properties.toSeq.map { p =>
      val field = component.fields.find(f => f._id == p._1)
      (field.get.name.get, p._2)
    }.toMap
  }

  def list(name: String) = Action {
    Async {
      val res = withConfiguration { cfg =>
       cfg.components.find(x => x.name.get == name) match {
          case None => Future.failed(BadComponent(name))
          case Some(c) => {
            val rawData = data.find(byId(c._id, "componentId")).cursor[Raw].toList
            for (rd <- rawData) yield rd.map(d => mapComponent(c, d))
          }
        }
      }

      jsonSerialize(res)
    }
  }
}
