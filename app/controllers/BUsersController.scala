package controllers

import javax.inject._
import models.daos.{AbstractBaseDAO, BaseDAO}
import models.entities._
import models.persistence.SlickTables._
import play.api._
import play.api.libs.functional.syntax._
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

  val ContentTypeJsonUtf8  = "application/json; charset=utf-8"
  val NotFoundResultBody   = """{"message":"NotFound"}"""
  val BadRequestResultBody = """{"message":"invalid json"}"""

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
  
  implicit val userStoreReads: Reads[User] = (
    (JsPath \ "id").read[Long] and
    (JsPath \ "email").readNullable[String] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "name_kana").readNullable[String] and
    (JsPath \ "team").readNullable[String] and
    (JsPath \ "hitotalent_id").readNullable[String] and
    (JsPath \ "gendar").readNullable[String] and
    (JsPath \ "age").readNullable[Long] and
    (JsPath \ "token" \ "access_token").readNullable[String] and
    (JsPath \ "token" \ "refresh_token").readNullable[String] and
    (JsPath \ "token" \ "access_token_expires_in").readNullable[String] and
    (JsPath \ "token" \ "refresh_token_expires_in").readNullable[String]
  )(User.apply _)

  /**
   */
  def index = Action.async {
    userDAO.findByFilter(_.isValid).map(result => {
      if(result.isEmpty) NotFound(NotFoundResultBody).as(ContentTypeJsonUtf8)
      else Ok(Json.toJson(result)).as(ContentTypeJsonUtf8)
    })
  }

  /**
   */
  def create = Action.async(parse.json) {
    request => {
      val requestBody = request.body.asOpt[User]

      requestBody.fold(
        Future { BadRequest(BadRequestResultBody).as(ContentTypeJsonUtf8) }
      )(user => {
        userDAO.insert(user).map(
          id => Ok(id.toString).as(ContentTypeJsonUtf8)
        )
      })
    }
  }

  /**
   */
  def get(id :Long) = Action.async {
    userDAO.findById(id).map(result => {
      result.fold(
        NotFound(NotFoundResultBody).as(ContentTypeJsonUtf8)
      )(
        user => Ok(Json.toJson(user)).as(ContentTypeJsonUtf8)
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
