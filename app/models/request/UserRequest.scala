package models.request

import models.request.TagRequest

case class UserRequest (
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
  tags:                  Option[Seq[TagRequest]]
) 
