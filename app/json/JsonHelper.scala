package json

import models.dtos._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonHelper {

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

  implicit val tokenDtoWrites = new Writes[TokenDto]{
    def writes(token: TokenDto) = Json.obj(
      "access_token"             -> token.accessToken,
      "refresh_token"            -> token.refreshToken,
      "access_token_expires_in"  -> token.accessTokenExpiresIn,
      "refresh_token_expires_in" -> token.refreshTokenExpiresIn
    )
  }

  implicit val userDtoWrites = new Writes[UserDto]{
    def writes(user: UserDto) = Json.obj(
      "id"                       -> user.id,
      "email"                    -> user.email,
      "name"                     -> user.name,
      "name_kana"                -> user.nameKana,
      "team"                     -> user.team,
      "hitotalent_id"            -> user.hitotalentId,
      "gendar"                   -> user.gendar,
      "age"                      -> user.age,
      "token"                    -> user.token,
      "tags"                     -> user.tags,
      "events"                   -> user.events,
      "biotops"                  -> user.biotops
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
  
  implicit val tokenDtoReads: Reads[TokenDto] = (
    (JsPath \ "access_token").readNullable[String] and
    (JsPath \ "refresh_token").readNullable[String] and
    (JsPath \ "access_token_expires_in").readNullable[String] and
    (JsPath \ "refresh_token_expires_in").readNullable[String]
  )(TokenDto.apply _)

  implicit val userDtoReads: Reads[UserDto] = (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "email").readNullable[String] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "name_kana").readNullable[String] and
    (JsPath \ "team").readNullable[String] and
    (JsPath \ "hitotalent_id").readNullable[String] and
    (JsPath \ "gendar").readNullable[String] and
    (JsPath \ "age").readNullable[Long] and
    (JsPath \ "token").readNullable[TokenDto] and
    (JsPath \ "tags").readNullable[Seq[TagDto]] and
    (JsPath \ "events").readNullable[Seq[EventDto]] and
    (JsPath \ "biotops").readNullable[Seq[BiotopDto]]
  )(UserDto.apply _)
}
