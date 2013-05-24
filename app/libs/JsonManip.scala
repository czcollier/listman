package libs

import play.api.libs.json.{JsObject, JsValue}

object JsonManip {

  implicit class WithDeepMerge(v: JsValue) {
    def deepMerge2(v2: JsValue) = WithDeepMerge.deepMerge2(v2, Some(v))
  }

  object WithDeepMerge {
    def deepMerge2(left: JsValue, right: Option[JsValue]): JsValue = {
      (left, right) match {
        case (l: JsObject, r) => {
          r match {
            case Some(v) => v match {
              case obj: JsObject => {
                val flds = l.fields.map { f => (f._1, deepMerge2(f._2, obj.value.get(f._1))) }
                JsObject(flds ++ obj.fields.filterNot(f => l.keys(f._1)))
              }
              case va: JsValue => l
            }
            case None => l
          }
        }
      }
    }
  }
}
