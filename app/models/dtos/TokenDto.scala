package models.dtos

case class TokenDto (
  accessToken:           Option[String],
  refreshToken:          Option[String],
  accessTokenExpiresIn:  Option[String],
  refreshTokenExpiresIn: Option[String]
) 
