package models.dtos

import models.entities.Event

case class EventDto (
  id:        Option[Long],
  date:      Option[String],
  eventType: Option[Long],
  numUsers:  Option[Long]
)

object EventDto {
  def create(
    event:    Event
  ) = {
    EventDto(
      Some(event.id),
      event.eventDate,
      event.eventType,
      None
    )
  }
}
