import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

class BUsrContorllerSpec extends PlaySpec with OneServerPerSuite {
 
  implicit override lazy val app = new GuiceApplicationBuilder().build()
  
  val WSClient            = app.injector.instanceOf[WSClient]
  val Localhost           = s"http://localhost:$port"
  val StoreUsersURL       = s"$Localhost/store/users"
  val ContentType         = "Content-Type"
  val ContentTypeJsonUtf8 = Some("application/json; charset=utf-8")
  
  /*********************************
   * Test with No Recoard Database *
   ********************************/

  /* GET */
  "GET /store/users returns NotFound when any user does not exists" in {
    val testURL = s"$StoreUsersURL"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }
  
  "GET /store/users?tagId={id} returns NotFound when anyone does not has argued tag" in {
    val testURL = s"$StoreUsersURL?tagId=999999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND 
  }
  
  "GET /store/users?eventId={id} returns NotFound when anyone does not has argued event" in {
    val testURL = s"$StoreUsersURL?eventId=999999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }
 
  "GET /store/users?biotopId={id} returns NotFound when anyone does not has argued biotop" in {
    val testURL = s"$StoreUsersURL?biotopId=999999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }
 
  "GET /store/users/{id} returns NotFound when any user does not exists" in {
    val testURL = s"$StoreUsersURL/999999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }
  
  /***********************************
   * Test with Some Recoard Database *
   **********************************/

  import json.JsonHelper
  import models.dtos._

  implicit val tagDtoWrites    = JsonHelper.tagDtoWrites
  implicit val eventDtoWrites  = JsonHelper.eventDtoWrites
  implicit val biotopDtoWrites = JsonHelper.biotopDtoWrites 
  implicit val userDtoWrites   = JsonHelper.userDtoWrites
  implicit val tagDtoReads     = JsonHelper.tagDtoReads
  implicit val eventDtoReads   = JsonHelper.eventDtoReads
  implicit val biotopDtoReads  = JsonHelper.biotopDtoReads
  implicit val userDtoReads    = JsonHelper.userDtoReads

  /* POST */
  "POST /store/users returns BAD_REQUEST when format is not valid Json" in {
    val testURL = s"$StoreUsersURL"
    val invalidJson = """
      {"dummy" : "this is invalid json..."}
    """
    val response = await(WSClient.url(testURL).post(Json.toJson(invalidJson)))

    response.header(ContentType) mustBe ContentTypeJsonUtf8
    response.status mustBe BAD_REQUEST
  }

  val requestUser_1 = UserDto(
    None,
    Some("hoge@hoge.com"),
    Some(" user1 "),
    Some("ホｹﾞ　"),
    Some("㍿√㌶"),
    Some("1"),
    Some("♂"),
    Some(30),
    Some(TokenDto(
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("2020-01-01 00:00:00.0"),
      Some("2020-01-01 00:00:00.0")
    )),
    Some(Nil),
    Some(Nil),
    Some(Nil)
  )
  
  "POST /store/users returns user information with id" in {
    val testURL = s"$StoreUsersURL"
    val requestBody = Json.toJson(requestUser_1)
    val response = await(WSClient.url(testURL).post(requestBody))

    response.header(ContentType) mustBe ContentTypeJsonUtf8
    response.status mustBe OK
    val responseUser = Json.parse(response.body).as[UserDto]
    responseUser.id           mustBe Option(1)
    responseUser.email        mustBe requestUser_1.email
    responseUser.name         mustBe requestUser_1.name
    responseUser.nameKana     mustBe requestUser_1.nameKana
    responseUser.team         mustBe requestUser_1.team
    responseUser.hitotalentId mustBe requestUser_1.hitotalentId
    responseUser.gendar       mustBe requestUser_1.gendar
    responseUser.age          mustBe requestUser_1.age
    responseUser.token        mustBe requestUser_1.token
    responseUser.tags         mustBe requestUser_1.tags
    responseUser.events       mustBe requestUser_1.events
    responseUser.biotops      mustBe requestUser_1.biotops
  }

  val requestUser_2 = UserDto(
    None,
    Some("hoge@hoge.com"),
    Some("James Hetfield"),
    Some("ジェームズ・ヘットフィールド"),
    Some("アメリカ営業所"),
    Some("12345"),
    Some("男"),
    Some(53),
    Some(TokenDto(
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("2020-01-01 00:00:00.0"),
      Some("2020-01-01 00:00:00.0")
    )),
    Some(Seq(
      TagDto(Some(1), Some("ボーカル"), Some(4)),
      TagDto(Some(2), Some("ギター"), Some(4)),
      TagDto(Some(3), Some("Metallica"), Some(4))
    )),
    Some(Seq(
      EventDto(Some(1), None, None, None) 
    )),
    Some(Seq(
      BiotopDto(Some(1), None, None) 
    ))
  )

  val requestUser_3 = UserDto(
    None,
    Some("hoge@hoge.com"),
    Some("Kirk Hammett"),
    Some("カーク・ハメット"),
    Some("アメリカ営業所"),
    Some("12345"),
    Some("男"),
    Some(53),
    Some(TokenDto(
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("2020-01-01 00:00:00.0"),
      Some("2020-01-01 00:00:00.0")
    )),
    Some(Seq(
      TagDto(Some(2), Some("ギター"), Some(4)),
      TagDto(Some(3), Some("Metallica"), Some(4))
    )),
    Some(Seq(
      EventDto(Some(2), None, None, None) 
    )),
    Some(Seq(
      BiotopDto(Some(2), None, None) 
    ))
  )

  /* GET */
  "GET /store/users returns all users" in {
    /* create some test users*/
    await(WSClient.url(StoreUsersURL).post(Json.toJson(requestUser_2)))
    await(WSClient.url(StoreUsersURL).post(Json.toJson(requestUser_3)))
    
    val testURL = s"$StoreUsersURL"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }
  
  "GET /store/users ignores illegal parameter format" in {
    val testURL = s"$StoreUsersURL?=null&=&?null"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }
  
  val requestUser_4 = UserDto(
    Some(999999),
    Some("hoge@hoge.com"),
    Some("Robert Trujillo"),
    Some("ロバート・トゥルージロ"),
    Some("アメリカ営業所"),
    Some("12345"),
    Some("男"),
    Some(53),
    Some(TokenDto(
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"),
      Some("2020-01-01 00:00:00.0"),
      Some("2020-01-01 00:00:00.0")
    )),
    Some(Seq(
      TagDto(Some(4), Some("ベース"), Some(4)),
      TagDto(Some(3), Some("Metallica"), Some(4))
    )),
    Some(Seq(
      EventDto(Some(3), None, None, None) 
    )),
    Some(Seq(
      BiotopDto(Some(3), None, None) 
    ))
  )

  /* PATCH */
  "PATCH /store/users/{id} update user information." in {
    val id = 1
    val testURL = s"$StoreUsersURL/$id"
    val requestBody = Json.toJson(requestUser_4)
    val response = await(WSClient.url(testURL).patch(requestBody))

    response.header(ContentType) mustBe ContentTypeJsonUtf8
    response.status mustBe OK
    val responseUser = Json.parse(response.body).as[UserDto]
    responseUser.id           mustBe Option(1)
    responseUser.email        mustBe requestUser_4.email
    responseUser.name         mustBe requestUser_4.name
    responseUser.nameKana     mustBe requestUser_4.nameKana
    responseUser.team         mustBe requestUser_4.team
    responseUser.hitotalentId mustBe requestUser_4.hitotalentId
    responseUser.gendar       mustBe requestUser_4.gendar
    responseUser.age          mustBe requestUser_4.age
    responseUser.token        mustBe requestUser_4.token
    responseUser.tags         mustBe requestUser_4.tags
    responseUser.events       mustBe requestUser_4.events
    responseUser.biotops      mustBe requestUser_4.biotops
  }

  "GET /store/users?tagId={id} searches users by tagId" in {
    val testURL = s"$StoreUsersURL?tagId=1"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUser = Json.parse(response.body).as[UserDto]
    responseUser.name mustBe requestUser_2.name
  }
  
  "GET /store/users?tagId={id} ignores when value is not a number" in {
    val testURL = s"$StoreUsersURL?tagId=1tag"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }
  
  "GET /store/users?tagId={id} ignores empty value" in {
    val testURL = s"$StoreUsersURL?tagId="
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }

  "GET /store/users?eventId={id} searches users by eventId" in {
    val testURL = s"$StoreUsersURL?eventId=1"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUser = Json.parse(response.body).as[UserDto]
    responseUser.name mustBe requestUser_2.name
  }
  
  "GET /store/users?eventId={id} ignores when value is not a number" in {
    val testURL = s"$StoreUsersURL?eventId=1event"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }
  
  "GET /store/users?eventId={id} ignores empty value" in {
    val testURL = s"$StoreUsersURL?eventId="
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }
  
  "GET /store/users?biotopId={id} searches users by biotopId" in {
    val testURL = s"$StoreUsersURL?biotopId=1"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUser = Json.parse(response.body).as[UserDto]
    responseUser.name mustBe requestUser_2.name
  }
  
  "GET /store/users?biotoptId={id} ignores when value is not a number" in {
    val testURL = s"$StoreUsersURL?biotopId=1biotop"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }
  
  "GET /store/users?biotopId={id} ignores empty value" in {
    val testURL = s"$StoreUsersURL?biotopId="
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 3
  }


  "GET /store/users/{id} returns stored user information when user exist" in {
    val id = 1
    val testURL = s"$StoreUsersURL/$id"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK 
    val responseUser = Json.parse(response.body).as[UserDto]
    responseUser.id           mustBe Option(1)
    responseUser.email        mustBe requestUser_1.email
    responseUser.name         mustBe requestUser_1.name
    responseUser.nameKana     mustBe requestUser_1.nameKana
    responseUser.team         mustBe requestUser_1.team
    responseUser.hitotalentId mustBe requestUser_1.hitotalentId
    responseUser.gendar       mustBe requestUser_1.gendar
    responseUser.age          mustBe requestUser_1.age
    responseUser.token        mustBe requestUser_1.token
    responseUser.tags         mustBe requestUser_1.tags
    responseUser.events       mustBe requestUser_1.events
    responseUser.biotops      mustBe requestUser_1.biotops
  }

  "GET /store/users/{id} ignores parameters" in {
    val id = 1
    val testURL = s"$StoreUsersURL/$id?tagId=tag&eventId=イベント&order=?&limit= &offset=null"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUser = Json.parse(response.body).as[UserDto]
    responseUser.id           mustBe Option(1)
    responseUser.email        mustBe requestUser_1.email
    responseUser.name         mustBe requestUser_1.name
    responseUser.nameKana     mustBe requestUser_1.nameKana
    responseUser.team         mustBe requestUser_1.team
    responseUser.hitotalentId mustBe requestUser_1.hitotalentId
    responseUser.gendar       mustBe requestUser_1.gendar
    responseUser.age          mustBe requestUser_1.age
    responseUser.token        mustBe requestUser_1.token
    responseUser.tags         mustBe requestUser_1.tags
    responseUser.events       mustBe requestUser_1.events
    responseUser.biotops      mustBe requestUser_1.biotops
  }

  /* DELETE */
  "DELETE /store/users/{id} delete user" in {
    val id = 1
    val testURL = s"$StoreUsersURL/$id"
    await(WSClient.url(testURL).delete)
    val response = await(WSClient.url(StoreUsersURL).get())
    
    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
    val responseUsers = Json.parse(response.body).as[Seq[UserDto]]
    responseUsers.size mustBe 2
  }

}
