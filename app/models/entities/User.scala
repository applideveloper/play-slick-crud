package models.entities

import models.dtos.UserDto

case class User (
  id:                    Long,
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
  refreshTokenExpiresIn: Option[String]
) extends BaseEntity
