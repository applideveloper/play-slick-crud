package models.daos

import models.entities._
import models.persistence.SlickTables._
import scala.concurrent.{Future, ExecutionContext, Await}
import scala.concurrent.duration._

class UserDAO (implicit ec: ExecutionContext) extends BaseDAO[UserTable,User] {
  import driver.api._
  
  def findUserWithOption(tag_id: Long, event_id: Long, biotop_id: Long):Future[Seq[StoreUser]] = {
    val users   = Await.result(db.run(userTableQ.result), 5000 millisecond)
    val tags    = Await.result(db.run(userTagMapTableQ.result), 5000 millisecond)
    val events  = Await.result(db.run(userEventMapTableQ.result), 5000 millisecond)
    val biotops = Await.result(db.run(userBiotopMapTableQ.result), 5000 millisecond)

    val storeUsers = users.map(user => {
      val tag    = tags.filter(_.userId == user.id)
      val event  = events.filter(_.userId == user.id)
      val biotop = biotops.filter(_.userId == user.id)
      StoreUser(user, Some(tag), Some(event), Some(biotop))
    })

    val result = storeUsers.filter(storeUser => {
      val containTag = if(tag_id != 0) storeUser.tags.fold(false)(_.filter(_.tagId == tag_id).size > 0) else true
      val containEvent = if(event_id != 0) storeUser.events.fold(false)(_.filter(_.eventId == event_id).size > 0) else true
      val containBiotop = if(biotop_id != 0) storeUser.biotops.fold(false)(_.filter(_.biotopId == biotop_id).size > 0) else true
      containTag && containEvent && containBiotop
    })

    val resultSize = result.size

    Future{result}
  }
}
