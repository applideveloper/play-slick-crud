package models.dtos

import models.entities._

case class UserDto (
  id:                    Option[Long],
  email:                 Option[String],
  name:                  Option[String],
  nameKana:              Option[String],
  team:                  Option[String],
  hitotalentId:          Option[String],
  gendar:                Option[String],
  age:                   Option[Long],
  accessToken:           Option[String],
  refreshToken:          Option[String],
  accessTokenExpiresIn:  Option[String],
  refreshTokenExpiresIn: Option[String],
  tags:                  Option[Seq[TagDto]],
  events:                Option[Seq[EventDto]],
  biotops:               Option[Seq[BiotopDto]]
) {
  def toEntity():User = {
    User(
      0,
      this.email,
      this.name,
      this.nameKana,
      this.team,
      this.hitotalentId,
      this.gendar,
      this.age,
      this.accessToken,
      this.refreshToken,
      this.accessTokenExpiresIn,
      this.refreshTokenExpiresIn
    )
  }
}

object UserDto {
  def create(
    user:    User,
    tags:    Option[Seq[Tag]],
    events:  Option[Seq[Event]],
    biotops: Option[Seq[Biotop]]
  ) = {
    val tagDtos    = tags.map(_.map(TagDto.create(_)))
    val eventDtos  = events.map(_.map(EventDto.create(_)))
    val biotopDtos = biotops.map(_.map(BiotopDto.create(_, None, None)))

    UserDto(
      Some(user.id),
      user.email,
      user.name,
      user.nameKana,
      user.team,
      user.hitotalentId,
      user.gendar,
      user.age,
      user.accessToken,
      user.refreshToken,
      user.accessTokenExpiresIn,
      user.refreshTokenExpiresIn,
      tagDtos,
      eventDtos,
      biotopDtos 
    )
  }
}
