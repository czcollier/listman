package controllers

import play.api.mvc._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import models.{FieldInfo, ComponentInfo, Configuration}
import scala.concurrent.Future
import ControllerHelp._
import play.api.libs.json.Json
import views.html
import reactivemongo.bson.{BSONDocument, BSONObjectID}

object ConfigUI extends JsonController with MongoController with Secured {

  import JsonCodec._

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def accept(id: String) = Action { implicit request =>
    forms.componentForm.bindFromRequest.fold(
      errors => BadRequest(errors.toString),
      comp => AsyncResult {
        val addFieldParm = request.body.asFormUrlEncoded.get("addField").headOption
        val addField = addFieldParm.getOrElse("false").toBoolean
        val newComp = if (addField)
          comp.copy(fields = new FieldInfo() :: comp.fields)
        else comp
        withConfiguration { cfg =>
          val comps = cfg.components.map {
            case x if x._id == newComp._id => newComp
            case y => y
          }
          val newCfg = cfg.copy(components = comps)
          cfgs.update(byId(stockId), newCfg).map { nd =>
            Redirect(routes.ConfigUI.get(newComp._id.get.stringify))
          }
        }
    })
  }


  def get(id: String) = withAuth { username => implicit request =>
    Async {
      val res = for {
        cfg <- cfgs.find(byId(stockId)).cursor[Configuration].toList
        component <- {
          cfg.headOption match {
            case None => Future.failed(BadSession)
            case Some(q) => {
              q.components.find(c => c._id.get.stringify.equals(id)) match {
                case None => Future(ComponentInfo(None, None, List()))
                case Some(c) =>  Future(c)
              }
            }
          }
        }
      } yield component

      res map { r =>
        Ok(views.html.componentView(
          r, forms.componentForm.fill(r)))
      }
    }
  }
}
