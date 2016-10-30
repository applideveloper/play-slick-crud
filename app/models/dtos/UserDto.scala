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
  token:                 Option[TokenDto],
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
      this.token.fold(Option(""))(t=>t.accessToken),
      this.token.fold(Option(""))(t=>t.refreshToken),
      this.token.fold(Option(""))(t=>t.accessTokenExpiresIn),
      this.token.fold(Option(""))(t=>t.refreshTokenExpiresIn)
    )
  }

  def isEmpty = 
    id.isEmpty           &&
    email.isEmpty        &&
    name.isEmpty         &&
    nameKana.isEmpty     &&
    team.isEmpty         &&
    hitotalentId.isEmpty &&
    gendar.isEmpty       &&
    age.isEmpty          &&
    token.isEmpty        &&
    tags.isEmpty         &&
    events.isEmpty       &&
    biotops.isEmpty
}

object UserDto {
  def create(
    user:    User,
    tags:    Option[Seq[Tag]],
    events:  Option[Seq[Event]],
    biotops: Option[Seq[Biotop]]
  ) = {
    val tokenDto   = Option(TokenDto(
      user.accessToken,
      user.refreshToken,
      user.accessTokenExpiresIn,
      user.refreshTokenExpiresIn
    ))
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
      tokenDto,
      tagDtos,
      eventDtos,
      biotopDtos 
    )
  }
}
