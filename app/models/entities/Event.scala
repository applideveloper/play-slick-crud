package models.entities

case class Event (
    id:        Long,
    eventDate: Option[String],
    eventType: Option[Long]
) extends BaseEntity
