package models.dtos

import models.entities._

case class BiotopDto (
  id:      Option[Long],
  event:   Option[EventDto],
  tag:     Option[TagDto]
) 

object BiotopDto {
  def create(
    biotop:    Biotop,
    event:     Option[Event],
    tag:       Option[Tag]
  ) = {
    val eventDto = event.map(EventDto.create(_))
    val tagDto = tag.map(TagDto.create(_))

    BiotopDto(
      Some(biotop.id),
      eventDto,
      tagDto
    )
  }
}
