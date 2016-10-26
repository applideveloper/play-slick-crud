package models.request

case class TagRequest (
  id:       Long,
  name:     Option[String],
  numUsers: Option[Long]
)
