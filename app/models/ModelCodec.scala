package models
/*
import reactivemongo.bson._
import scala.collection.mutable.ListBuffer
import reactivemongo.bson.DefaultBSONHandlers._

object ModelCodec {
  implicit object FieldInfoCodec extends BSONDocumentReader[FieldInfo] with BSONDocumentWriter[FieldInfo] {
    def read(doc: BSONDocument): FieldInfo = {
      FieldInfo(
        doc.getAs[String]("name"),
        doc.getAs[String]("dataType")
      )
    }

    def write(fld: FieldInfo) = {
      BSONDocument(
        "name" -> fld.name,
        "dataType" -> fld.dataType
      )
    }
  }

  implicit object FieldsCodec extends MapCodec[FieldInfo]

  abstract class MapCodec[T](implicit reader: BSONDocumentReader[T], writer: BSONDocumentWriter[T])
      extends BSONDocumentReader[Map[String, T]] with BSONDocumentWriter[Map[String,T]] {
    def read(d: BSONDocument): Map[String, T] = {
      d.elements.map(e => (e._1, d.getAs[T](e._1).get)).toMap
    }

    def write(m: Map[String, T]): BSONDocument = {
      val producers: ListBuffer[Producer[(String, BSONValue)]] = new ListBuffer()
      for (f <- m) {
        producers.append(f)
      }
      BSONDocument(producers: _*)
    }
  }

  abstract class ValMapCodec[T](implicit reader: BSONReader[_ <: BSONValue, T], writer: BSONWriter[T, _ <: BSONValue])
    extends BSONDocumentReader[Map[String, T]] with BSONDocumentWriter[Map[String,T]] {
    def read(d: BSONDocument): Map[String, T] = {
      d.elements.map(e => (e._1, d.getAs[T](e._1).get)).toMap
    }

    def write(m: Map[String, T]): BSONDocument = {
      val producers: ListBuffer[Producer[(String, BSONValue)]] = new ListBuffer()
      for (f <- m) {
        producers.append(f)
      }
      BSONDocument(producers: _*)
    }
  }
  implicit object ComponentCodec extends BSONDocumentReader[ComponentInfo] with BSONDocumentWriter[ComponentInfo] {
    def read(doc: BSONDocument): ComponentInfo = {
      ComponentInfo(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[Map[String, FieldInfo]]("fields").getOrElse(Map())
      )
    }

    def write(comp: ComponentInfo) = {
      BSONDocument(
        "fields" -> comp.fields
      )
    }
  }

  implicit object ComponentsCodec extends MapCodec[ComponentInfo]

  implicit object ConfigurationCodec extends BSONDocumentReader[Configuration] with BSONDocumentWriter[Configuration] {
    def read(doc: BSONDocument): Configuration = {
      Configuration(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("name"),
        doc.getAs[Map[String, ComponentInfo]]("components").getOrElse(Map())
      )
    }

    def write(configuration: Configuration) = {
      BSONDocument(
        "_id" -> configuration._id.getOrElse(BSONObjectID.generate),
        "name" -> configuration.name,
        "components" -> configuration.components
      )
    }
  }

  implicit object PropertiesCodec extends ValMapCodec[BSONValue]

  implicit object RawCodec extends BSONDocumentReader[Raw] with BSONDocumentWriter[Raw] {
    def read(doc: BSONDocument): Raw = {
      Raw(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONObjectID]("componentId"),
        doc.getAs[Map[String, BSONValue]]("properties").getOrElse(Map())
      )
    }

    def write(r: Raw) = {
      BSONDocument(
        "_id" -> r.id,
        "componentId" -> r.componentId.getOrElse(BSONObjectID.generate),
        "properties" -> r.properties
      )
    }
  }

  implicit object ResCodec extends BSONDocumentWriter[Res] {
    def write(r: Res) = {
      BSONDocument("OK" -> r.OK, "msg" -> r.msg)
    }
  }
}
*/
