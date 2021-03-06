package controllers

import play.api.mvc.Action
import play.api.libs.json._
import libs.JsonManip._

object Experiments extends JsonController {

  val mj = JsString("foo")
  val mj2 = JsString("bar")

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

  object JsUndefine extends JsUndefined("no value")

  val yourJson = Json.obj(
    "key1" -> "newValue1",
    "key2" -> Json.obj(
      "key21" -> 121213,
      "key23" -> Json.arr("alpha", "bravo", "charlie"),
      "key24" -> Json.obj("key241" -> JsUndefine, "key243" -> false)
    ),
    "key4" -> 235
  )


  def merge3 = Action {
    val nj = myJson.deepMerge(yourJson)
    Ok(Json.prettyPrint(Json.toJson(nj)))
  }

  def merge2 = Action {
    val nj = myJson.deepMerge2(yourJson)
    Ok(Json.prettyPrint(Json.toJson(nj)))
  }

  def merge = Action {
    val xfrm = (__).json.update(__.read[JsObject]).map { obj => obj ++ yourJson }
    val newJson = myJson.transform(xfrm)
    Ok(Json.prettyPrint(Json.toJson(newJson.asOpt.get)))
  }
}
