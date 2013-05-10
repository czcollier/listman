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

object Auth extends Controller with MongoController {

  import JsonCodec._

  private def users: JSONCollection = db.collection[JSONCollection]("users")

  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> text
    ) verifying("Invalid user name or password", fields => fields match {
      case (e, p) => {
        Await.result(getUser(e, p).map(x => x match {
          case Some(y) => true
          case None => false
        }), 1.second)
      }
    })
  )

  def getUser(username: String, password: String): Future[Option[User]] = {
    val query = Json.obj("username" -> username, "password" -> password)
    for {
      u <- users.find(query).cursor[User].headOption
    } yield u
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.ConfigUI.index()).withSession(Security.username -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }
}
trait Secured {

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login())

  def withAuth(f: => String => Request[AnyContent] => Result) = {
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
