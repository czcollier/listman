package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views.html
import models.User
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import play.api.cache.Cache
import java.util.UUID
import controllers.ControllerHelp.BadUser
import play.api.data.validation.Constraints._

object Auth extends Controller with MongoController {
  import JsonCodec._

  private def users: JSONCollection = db.collection[JSONCollection]("users")

  val loginForm = Form(tuple(
    "email" -> email.verifying(nonEmpty),
    "password" -> text
  ))

  def getUser(username: String, password: String): Future[(UUID, User)] = {
    val query = Json.obj("username" -> username, "password" -> password)

    for {
      qr <- users.find(query).cursor[User].toList
      first <- qr.headOption match {
        case Some(u) => Future(UUID.randomUUID, u)
        case None => Future.failed(BadUser)
      }
    } yield first
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }


  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errs => BadRequest(html.login(errs)),
      good => Await.result(getUser(good._1, good._2).map {
        sess => {
          import play.api.Play.current
          Cache.set(sess._1.toString, sess._2)
          Redirect(routes.ConfigUI.index()).withSession(Security.username -> sess._1.toString)
        }
      }
      recover {
        case e => {
          BadRequest(html.login(loginForm.fill(good).withError("", "invalid credentials")))
        }
      }, 1.second)
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}
trait Secured {

  def username(request: RequestHeader)(implicit app: play.api.Application): Option[User] = {
    request.session.get(Security.username) match {
      case Some(id) => Cache.get(id).asInstanceOf[Option[User]]
      case None => None
    }
  }

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login())

  def withAuth(f: => User => Request[AnyContent] => Result) = {
    import play.api.Play.current
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  /**
   * This method shows how you could wrap the withAuth method to also fetch your user
   * You will need to implement UserDAO.findOneByUsername
   */
//  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
//    Some(User(Some(BSONObjectID.generate), "ccollier", "secret")).map { user =>
//      f(user)(request)
//    }.getOrElse(onUnauthorized(request))
//  }
}
