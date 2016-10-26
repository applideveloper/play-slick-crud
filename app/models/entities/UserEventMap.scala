package models.entities

case class UserEventMap (
    id:      Long,
    userId:  Long,
    eventId: Long 
) extends BaseEntity
