package models.entities

case class UserBiotopMap (
    id:       Long,
    userId:   Long,
    biotopId: Long 
) extends BaseEntity

