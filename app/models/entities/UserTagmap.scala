package models.entities

case class UserTagMap (
    id:     Long,
    userId: Long,
    tagId:  Long 
) extends BaseEntity
