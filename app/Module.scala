import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Provides}
import java.time.Clock

import models.daos.{AbstractBaseDAO, BaseDAO, UserDAO}
import models.entities._
import models.persistence.SlickTables
import models.persistence.SlickTables._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  import SlickTables.userTableQ
  import SlickTables.tagTableQ
  import SlickTables.eventTableQ
  import SlickTables.biotopTableQ
  import SlickTables.userTagMapTableQ
  import SlickTables.userEventMapTableQ
  import SlickTables.userBiotopMapTableQ

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
  }

  @Provides def provideUserDAO:          UserDAO                                           = new UserDAO
  @Provides def provideTagDAO:           AbstractBaseDAO[TagTable,Tag]                     = new BaseDAO[TagTable,models.entities.Tag]
  @Provides def provideEventDAO:         AbstractBaseDAO[EventTable,Event]                 = new BaseDAO[EventTable,Event]
  @Provides def provideBiotopDAO:        AbstractBaseDAO[BiotopTable,Biotop]               = new BaseDAO[BiotopTable,Biotop]
  @Provides def provideUserTagMapDAO:    AbstractBaseDAO[UserTagMapTable,UserTagMap]       = new BaseDAO[UserTagMapTable,UserTagMap]
  @Provides def provideUserEventMapDAO:  AbstractBaseDAO[UserEventMapTable,UserEventMap]   = new BaseDAO[UserEventMapTable,UserEventMap]
  @Provides def provideUserBiotopMapDAO: AbstractBaseDAO[UserBiotopMapTable,UserBiotopMap] = new BaseDAO[UserBiotopMapTable,UserBiotopMap]


} 
