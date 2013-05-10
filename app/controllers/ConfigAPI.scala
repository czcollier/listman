package controllers

import play.modules.reactivemongo.MongoController
import models.{ComponentInfo, Configuration}
import scala.concurrent.Future
import controllers.ControllerHelp._
import reactivemongo.bson.BSONObjectID
import play.api.mvc.Action

object ConfigAPI extends JsonController with MongoController with Secured {

  import JsonCodec._

  def configuration = Action {
    Async {
      withConfiguration { cfg =>
        jsonSerialize(Future(cfg))
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
