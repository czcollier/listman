package controllers

import play.api.mvc.Action
import play.api.libs.json._

object Experiments extends JsonController {

  val myJson = Json.obj(
    "key1" -> "value1",
    "key2" -> Json.obj(
      "key21" -> 123,
      "key22" -> true,
      "key23" -> Json.arr("alpha", "beta", "gamma"),
      "key24" -> Json.obj(
        "key241" -> 234.123,
        "key242" -> "value242"
      )
    ),
    "key3" -> 234
  )

  val yourJson = Json.obj(
    "key1" -> "newValue1",
    "key2" -> Json.obj(
      "key21" -> 121213,
      "key23" -> Json.arr("alpha", "bravo", "charlie")
    ),
    "key4" -> 235
  )

  def rMerge(obj: JsObject) = {
    (__).json.update(__.read[JsObject]).map { o =>
      o.fields.map { f =>
        f._2 match {
          case no: JsObject => 
        }
      }
    }
  }
  def merge = Action {
    val xfrm = (__).json.update(__.read[JsObject]).map { obj => obj ++ yourJson }
    val newJson = myJson.transform(xfrm)
    Ok(Json.prettyPrint(Json.toJson(newJson.asOpt.get)))
  }
}
