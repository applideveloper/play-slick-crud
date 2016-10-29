package controllers

import javax.inject._
import models.daos.{AbstractBaseDAO, BaseDAO}
import models.entities._
import models.dtos._
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

  implicit val tagDtoWrites = new Writes[TagDto]{
    def writes(tag: TagDto) = Json.obj(
      "id"        -> tag.id,
      "name"      -> tag.name,
      "num_users" -> tag.numUsers
    )
  }

  implicit val eventDtoWrites = new Writes[EventDto]{
    def writes(event: EventDto) = Json.obj(
      "id"        -> event.id,
      "date"      -> event.date,
      "type"      -> event.eventType,
      "num_users" -> event.numUsers
    )
  }
  
  implicit val biotopDtoWrites = new Writes[BiotopDto]{
    def writes(biotop: BiotopDto) = Json.obj(
      "id"        -> biotop.id,
      "event"     -> biotop.event,
      "tag"       -> biotop.tag
    )
  }

  implicit val userDtoWrites = new Writes[UserDto]{
    def writes(user: UserDto) = Json.obj(
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
      "refreshTokenExpiresIn" -> user.refreshTokenExpiresIn,
      "tags"                  -> user.tags,
      "events"                -> user.events,
      "biotops"               -> user.biotops
    )
  }

  implicit val tagDtoReads: Reads[TagDto] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "num_users").readNullable[Long]
  )(TagDto.apply _)

  implicit val eventDtoReads: Reads[EventDto] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "date").readNullable[String] and
    (JsPath \ "type").readNullable[Long] and
    (JsPath \ "num_users").readNullable[Long]
  )(EventDto.apply _)

  implicit val biotopDtoReads: Reads[BiotopDto] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "event").readNullable[EventDto] and
    (JsPath \ "tag").readNullable[TagDto]
  )(BiotopDto.apply _)

  implicit val userDtoReads: Reads[UserDto] = (
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
    (JsPath \ "tags").readNullable[Seq[TagDto]] and
    (JsPath \ "events").readNullable[Seq[EventDto]] and
    (JsPath \ "biotops").readNullable[Seq[BiotopDto]]
  )(UserDto.apply _)

  /**
   */
  def index = Action.async {
    userDAO.findByFilter(_.isValid).map(userResult => {
      if(userResult.isEmpty) NotFound(NotFoundResultBody).as(ContentTypeJsonUtf8)
      else {
        val userResponse = userResult.map(
          UserDto.create(_, Some(Nil), Some(Nil), Some(Nil))
        )
        Ok(Json.toJson(userResponse)).as(ContentTypeJsonUtf8)
      }
    })
  }

  /**
   */
  def create = Action.async(parse.json) {
    request => {
      request.body.asOpt[UserDto].fold(
        Future { BadRequest(BadRequestResultBody).as(ContentTypeJsonUtf8) }
      )(userRequest => {
        userDAO.insert(userRequest.toEntity).andThen{
          case Success(id) => {
            userRequest.tags.fold()(tags => {
              tags.foreach(tag => {
                tag.id.fold()(tId => {
                  userTagMapDAO.insert(UserTagMap(0, id, tId))
                })
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
    userDAO.findById(id).map(_.fold(
        NotFound(NotFoundResultBody).as(ContentTypeJsonUtf8)
      )(user => {
        val userResponse = UserDto.create(user, Some(Nil), Some(Nil), Some(Nil))
        Ok(Json.toJson(userResponse)).as(ContentTypeJsonUtf8)
      })
    )
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
