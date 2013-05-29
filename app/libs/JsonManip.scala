package libs

import play.api.libs.json.{JsObject, JsValue}

object JsonManip {

  implicit class WithDeepMerge(v: JsValue) {
    def deepMerge2(v2: JsValue) = WithDeepMerge.deepMerge2(v2, v)
  }

  object WithDeepMerge {

    def deepMerge2(left: JsValue, right: JsValue): JsValue = {
      (left, right) match {
        case (l: JsObject, r: JsObject) => {
          val flds = l.fields.map { field => {
            val value = r.value.get(field._1) match {
              case Some(x) => deepMerge2(field._2, x)
              case None => field._2
            }
            (field._1, value)
          }}
          JsObject(flds ++ r.fields.filterNot(f => l.keys(f._1)))
        }
        case (l, r) => l
      }
    }
  }
}
