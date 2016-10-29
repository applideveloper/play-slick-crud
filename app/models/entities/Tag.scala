package models.entities

case class Tag (
  id:    Long,
  name:  Option[String],
  alias: Option[String]
) extends BaseEntity
