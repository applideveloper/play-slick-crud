package models.dtos

import models.entities.Tag

case class TagDto (
  id:       Option[Long],
  name:     Option[String],
  numUsers: Option[Long]
) {
  def toEntity(): Tag = {
    Tag(this.id.fold(0L)(i=>i), this.name, Option.empty)
  }
}

object TagDto {
  def create(
    tag:    Tag
  ) = {
    TagDto(
      Some(tag.id),
      tag.name,
      None
    )
  }
}
