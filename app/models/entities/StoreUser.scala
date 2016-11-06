package models.entities

import models.entities._

case class StoreUser (
  user:    User,
  tags:    Option[Seq[UserTagMap]],
  events:  Option[Seq[UserEventMap]],
  biotops: Option[Seq[UserBiotopMap]]
)// extends BaseEntity
