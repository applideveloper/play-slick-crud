package controllers

import javax.inject._
import models.daos.{AbstractBaseDAO, BaseDAO}
import models.entities._
import models.persistence.SlickTables._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class BUsersController @Inject()(
  userDAO: AbstractBaseDAO[UserTable, User],
  tagDAO: AbstractBaseDAO[TagTable, Tag]
)(
  implicit exec: ExecutionContext
) extends Controller {

  implicit val userStoreWrites = new Writes[User]{
    def writes(user: User) = Json.obj(
      "id"                    -> user.id,
      "email"                 -> user.email,
      "name"                  -> user.name,
      "nameKana"              -> user.nameKana,
      "team"                  -> user.team,
      "hitotalentId"          -> user.hitotalentId,
      "gendar"                -> user.gendar,
      "age"                   -> user.age,
      "accessToken"           -> user.accessToken,
      "refreshToken"          -> user.refreshToken,
      "accessTokenExpiresIn"  -> user.accessTokenExpiresIn,
      "refreshTokenExpiresIn" -> user.refreshTokenExpiresIn
    )
  }
  
  /**
   */
  def index = Action.async {
    userDAO.findByFilter(_.isValid).map(result => {
      if(result.isEmpty) NotFound("{}")
      else Ok(Json.toJson(result))
    })
  }

  /**
   */
  //def create = Action.async(parse.json) {
  def create = Action {
    BadRequest(s"BUsersController#create is not implemented yet.")
  }

  /**
   */
  def get(id :Long) = Action.async {
    userDAO.findById(id).map(result => {
      result.fold(
        NotFound("{}")
      )(
        user => Ok(Json.toJson(user))
      )
    })
  }

  /**
   */
  def patch(id :Long) = Action {
    BadRequest(s"BUsersController#patch is not implemented yet.")
  }

  def delete(id :Long) = Action {
    BadRequest(s"BUsersController#delete is not implemented yet.")
  }

}
