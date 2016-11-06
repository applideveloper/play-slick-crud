package controllers

import json.JsonHelper
import javax.inject._
import models.daos.{AbstractBaseDAO, BaseDAO, UserDAO}
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
  userDAO:          UserDAO,
  userTagMapDAO:    AbstractBaseDAO[UserTagMapTable, UserTagMap],
  userEventMapDAO:  AbstractBaseDAO[UserEventMapTable, UserEventMap],
  userBiotopMapDAO: AbstractBaseDAO[UserBiotopMapTable, UserBiotopMap]
)(
  implicit exec: ExecutionContext
) extends Controller {

  val ContentTypeJsonUtf8  = "application/json; charset=utf-8"
  val NotFoundResultBody   = """{"message":"NotFound"}"""
  val BadRequestResultBody = """{"message":"invalid json"}"""
  val NotFoundResult       = NotFound(NotFoundResultBody).as(ContentTypeJsonUtf8)
  val BadRequestResult     = BadRequest(BadRequestResultBody).as(ContentTypeJsonUtf8)
  
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
  def index(tag_id: Long, event_id: Long, biotop_id: Long) = Action.async {
    val usersResult = userDAO.findUserWithOption(tag_id, event_id, biotop_id)
    val usersResponse = usersResult.map(_.map(userResult => {
      val user    = userResult.user
      val tags    = userResult.tags.map(_.map(t=>Tag(t.tagId, None, None)))
      val events  = userResult.events.map(_.map(e=>Event(e.eventId, None, None)))
      val biotops = userResult.biotops.map(_.map(b=>Biotop(b.biotopId, None, None)))
      UserDto.create(user, tags, events, biotops)
    }))
    usersResponse.map(users => {
      if(users.size == 0) NotFoundResult
      else if(users.size == 1) Ok(Json.toJson(users(0))).as(ContentTypeJsonUtf8)
      else Ok(Json.toJson(users)).as(ContentTypeJsonUtf8)
    })
  }

  def create = Action.async(parse.json) {
    request => {
      request.body.asOpt[UserDto].fold(
        Future { BadRequestResult } 
      )(userRequest => {
        if(userRequest.isEmpty) Future { BadRequestResult }
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
              userRequest.events.fold()(events => {
                events.foreach(event => {
                  event.id.fold()(eId => {
                    userEventMapDAO.insert(UserEventMap(0, id, eId))
                  })
                })
              })
              userRequest.biotops.fold()(biotops => {
                biotops.foreach(biotop => {
                  biotop.id.fold()(bId => {
                    userBiotopMapDAO.insert(UserBiotopMap(0, id, bId))
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
        NotFoundResult
      )(user => {
        val userResponse = UserDto.create(user, Some(Nil), Some(Nil), Some(Nil))
        Ok(Json.toJson(userResponse)).as(ContentTypeJsonUtf8)
      })
    )
  }

  /**
   */
  def patch(id :Long) = Action.async(parse.json) {
    request => {
      request.body.asOpt[UserDto].fold(
        Future { BadRequestResult } 
      )(userRequest => {
        if(userRequest.isEmpty) Future { BadRequestResult }
        else {
          userDAO.findById(id).map(user => {
            user.fold(NotFoundResult)(u => {
              userDAO.update(userRequest.toEntity).andThen{
                case Success(id) => {
                  userTagMapDAO.deleteByFilter(_.userId == id)
                  userRequest.tags.fold()(tags => {
                    tags.foreach(tag => {
                      tag.id.fold()(tId => {
                        userTagMapDAO.insert(UserTagMap(0, id, tId))
                      })
                    })
                  })
                  userEventMapDAO.deleteByFilter(_.userId == id)
                  userRequest.events.fold()(events => {
                    events.foreach(event => {
                      event.id.fold()(eId => {
                        userEventMapDAO.insert(UserEventMap(0, id, eId))
                      })
                    })
                  })
                  userBiotopMapDAO.deleteByFilter(_.userId == id)
                  userRequest.biotops.fold()(biotops => {
                    biotops.foreach(biotop => {
                      biotop.id.fold()(bId => {
                        userBiotopMapDAO.insert(UserBiotopMap(0, id, bId))
                      })
                    })
                  })
                }
              }
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
          })
        }
      })
    }
  }

  def delete(id :Long) = Action.async {
    userDAO.deleteById(id).map(id => {
      if(id == 0) NotFoundResult
      else Ok(id.toString).as(ContentTypeJsonUtf8)
    })
  }

}
