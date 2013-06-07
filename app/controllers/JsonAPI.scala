package controllers

import play.modules.reactivemongo.MongoController
import play.api.libs.json.JsObject
import play.modules.reactivemongo.json.collection.JSONCollection

object JsonAPI extends JsonController with MongoController with Secured {
  private def collection: JSONCollection = db.collection[JSONCollection]("data")

  def find(query: String) = withAuth { sess => implicit request => {
    Async {
      val res = collection.find(query).cursor[JsObject].toList
      jsonSerialize(res)
    }
  }}
}
