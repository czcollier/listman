package controllers

import play.api.mvc._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import models.{ComponentInfo, Configuration}
import scala.concurrent.Future
import ControllerHelp._
import play.api.libs.json.Json
import views.html

object Application extends Controller with MongoController {
  private def cfgs: JSONCollection = db.collection[JSONCollection]("configs")

  import JsonCodec._

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def accept = Action { implicit request =>
    forms.componentForm.bindFromRequest.fold(
      errors => BadRequest(errors.toString),
      comp => Ok(html.componentView(
        comp, forms.componentForm.fill(comp)
    )))
  }

  def get(id: String) = Action {
    Async {
      println("==========> " + id)
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

      for (r <- res)
        yield { println(r);Ok(views.html.componentView(
          r, forms.componentForm.fill(r))) }
    }
  }
}
