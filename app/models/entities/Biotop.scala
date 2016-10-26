package models.entities

case class Biotop (
    id:      Long,
    eventId: Option[Long],
    tagId:   Option[Long]
) extends BaseEntity
