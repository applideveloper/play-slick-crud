package models.persistence

import models.entities._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

/**
  * The companion object.
  */
object SlickTables extends HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  abstract class BaseTable[T](tag: Tag, schema: Some[String], name: String) extends Table[T](tag, schema, name) {
    def id                    = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def isValid               = column[Boolean]("IS_VALID")
  }

  class UserTable (tag: Tag) extends BaseTable[User](tag, Some("STORE"), "USERS") {
    def email                 = column[Option[String]]("EMAIL")
    def name                  = column[Option[String]]("NAME")
    def nameKana              = column[Option[String]]("NAME_KANA")
    def team                  = column[Option[String]]("TEAM")
    def hitotalentId          = column[Option[String]]("HITOTALENT_ID")
    def gendar                = column[Option[String]]("GENDAR")
    def age                   = column[Option[Long]]("AGE")
    def accessToken           = column[Option[String]]("ACCESS_TOKEN")
    def refreshToken          = column[Option[String]]("REFRESH_TOKEN")
    def accessTokenExpiresIn  = column[Option[String]]("ACCESS_TOKEN_EXPIRES_IN")
    def refreshTokenExpiresIn = column[Option[String]]("REFRESH_TOKEN_EXPIRES_IN")
    def * = (
      id,
      email,
      name,
      nameKana,
      team,
      hitotalentId,
      gendar,
      age,
      accessToken,
      refreshToken,
      accessTokenExpiresIn,
      refreshTokenExpiresIn
    ) <> (User.tupled, User.unapply)
  }

  class TagTable (tag: Tag) extends BaseTable[models.entities.Tag](tag, Some("STORE"), "TAG") {
    def name                  = column[Option[String]]("NAME")
    def alias                 = column[Option[String]]("ALIAS")
    def * = (
      id,
      name, 
      alias
    ) <> (Tag.tupled, Tag.unapply)
  }

  class EventTable (tag: Tag) extends BaseTable[Event](tag, Some("STORE"), "EVENT") {
    def eventDate             = column[Option[String]]("EVENT_DATE")
    def eventType             = column[Option[Long]]("EVENT_TYPE")
    def * = (
      id,
      eventDate, 
      eventType
    ) <> (Event.tupled, Event.unapply)
  }
  
  class BiotopTable (tag: Tag) extends BaseTable[Biotop](tag, Some("STORE"), "BIOTOP") {
    def eventId               = column[Option[Long]]("EVENT_ID")
    def tagId                 = column[Option[Long]]("TAG_ID")
    def * = (
      id,
      eventId,
      tagId
    ) <> (Biotop.tupled, Biotop.unapply)
  }
  
  class UserTagMapTable (tag: Tag) extends BaseTable[UserTagMap](tag, Some("STORE"), "USER_TAG_MAP") {
    def userId                = column[Long]("USER_ID")
    def tagId                 = column[Long]("TAG_ID")
    def user                  = foreignKey("USER_TAG_MAP_FK", userId, userTableQ)(_.id)
    def * = (
      id,
      userId, 
      tagId
    ) <> (UserTagMap.tupled, UserTagMap.unapply)
  }

  class UserEventMapTable (tag: Tag) extends BaseTable[UserEventMap](tag, Some("STORE"), "USER_EVENT_MAP") {
    def userId                = column[Long]("USER_ID")
    def eventId               = column[Long]("EVENT_ID")
    def * = (
      id,
      userId,
      eventId
    ) <> (UserEventMap.tupled, UserEventMap.unapply)
  }

  class UserBiotopMapTable (tag: Tag) extends BaseTable[UserBiotopMap](tag, Some("STORE"), "USER_BIOTOP_MAP") {
    def userId                = column[Long]("USER_ID")
    def biotopId              = column[Long]("BIOTOP_ID")
    def * = (
      id,
      userId, 
      biotopId
    ) <> (UserBiotopMap.tupled, UserBiotopMap.unapply)
  }

  implicit val userTableQ:          TableQuery[UserTable] = TableQuery[UserTable]
  implicit val tagTableQ:           TableQuery[TagTable] = TableQuery[TagTable]
  implicit val eventTableQ:         TableQuery[EventTable] = TableQuery[EventTable]
  implicit val biotopTableQ:        TableQuery[BiotopTable] = TableQuery[BiotopTable]
  implicit val userTagMapTableQ:    TableQuery[UserTagMapTable] = TableQuery[UserTagMapTable]
  implicit val userEventMapTableQ:  TableQuery[UserEventMapTable] = TableQuery[UserEventMapTable]
  implicit val userBiotopMapTableQ: TableQuery[UserBiotopMapTable] = TableQuery[UserBiotopMapTable]
}
