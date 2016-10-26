package controllers

import javax.inject._
import models.daos.{AbstractBaseDAO, BaseDAO}
import models.entities._
import models.request._
import models.persistence.SlickTables._
import play.api._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext, Await}
import scala.concurrent.duration._
import scala.util.Success

@Singleton
class BUsersController @Inject()(
  userDAO: AbstractBaseDAO[UserTable, User],
  userTagMapDAO: AbstractBaseDAO[UserTagMapTable, UserTagMap]
)(
  implicit exec: ExecutionContext
) extends Controller {

  val ContentTypeJsonUtf8  = "application/json; charset=utf-8"
  val NotFoundResultBody   = """{"message":"NotFound"}"""
  val BadRequestResultBody = """{"message":"invalid json"}"""

  implicit val userWrites = new Writes[User]{
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
  
  implicit val tagRequestReads: Reads[TagRequest] = (
    (JsPath \ "id").read[Long] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "num_users").readNullable[Long]
  )(TagRequest.apply _)

  implicit val userRequestReads: Reads[UserRequest] = (
    (JsPath \ "id").readNullable[Long] and
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
    (JsPath \ "token" \ "refresh_token_expires_in").readNullable[String] and
    (JsPath \ "tags").readNullable[Seq[TagRequest]]
  )(UserRequest.apply _)

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
      val optionalUserRequest = request.body.asOpt[UserRequest]

      optionalUserRequest.fold(
        Future { BadRequest(BadRequestResultBody).as(ContentTypeJsonUtf8) }
      )(userRequest => {
        val user = User(0, userRequest.email, userRequest.name, userRequest.nameKana, userRequest.team, userRequest.hitotalentId, userRequest.gendar, userRequest.age, userRequest.accessToken, userRequest.refreshToken, userRequest.accessTokenExpiresIn, userRequest.refreshTokenExpiresIn)
        userDAO.insert(user).andThen{
          case Success(id) => {
            userRequest.tags.fold(
            )(tags => {
              tags.foreach(tag => {
                val utm = UserTagMap(0, id, tag.id)
                userTagMapDAO.insert(utm)
              })
            })
          }
        }.map(id => {
          Ok(id.toString).as(ContentTypeJsonUtf8)
        })
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
  def patch(id :Long) = Action.async {
    Future { BadRequest(s"BUsersController#patch is not implemented yet.") }
  }

  def delete(id :Long) = Action.async {
    Future { BadRequest(s"BUsersController#delete is not implemented yet.") }
  }

}
