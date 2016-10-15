package models.entities

case class Tag (
    name:  Option[String],
    alias: Option[String]
) extends BaseEntity
