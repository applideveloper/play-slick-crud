package models.entities

case class Event (
    eventDate: Option[String],
    eventType: Option[Long]
) extends BaseEntity
