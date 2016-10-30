package controllers

import json.JsonHelper
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

  implicit val tagDtoWrites    = JsonHelper.tagDtoWrites
  implicit val eventDtoWrites  = JsonHelper.eventDtoWrites
  implicit val biotopDtoWrites = JsonHelper.biotopDtoWrites 
  implicit val userDtoWrites   = JsonHelper.userDtoWrites
  implicit val tagDtoReads     = JsonHelper.tagDtoReads
  implicit val eventDtoReads   = JsonHelper.eventDtoReads
  implicit val biotopDtoReads  = JsonHelper.biotopDtoReads
  implicit val userDtoReads    = JsonHelper.userDtoReads
  
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
        if(userRequest.isEmpty) Future { BadRequest(BadRequestResultBody).as(ContentTypeJsonUtf8) }
        else {
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
            Ok(Json.toJson(
              UserDto(
                Option(id),
                userRequest.email,
                userRequest.name,
                userRequest.nameKana,
                userRequest.team,
                userRequest.hitotalentId,
                userRequest.gendar,
                userRequest.age,
                userRequest.token,
                userRequest.tags,
                userRequest.events,
                userRequest.biotops
              )
            )).as(ContentTypeJsonUtf8)
          })
        }
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
