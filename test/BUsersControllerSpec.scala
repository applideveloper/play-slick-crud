import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class BUsrContorllerSpec extends PlaySpec with OneServerPerSuite {
 
  implicit override lazy val app = new GuiceApplicationBuilder().build()
  
  val WSClient            = app.injector.instanceOf[WSClient]
  val Localhost           = s"http://localhost:$port"
  val StoreUsersURL       = s"$Localhost/store/users"
  val ContentType         = "Content-Type"
  val ContentTypeJsonUtf8 = Some("application/json; charset=utf-8")

  /************************
   * GET /store/users (1) *
   ************************/
  "GET /store/users returns NotFound when any user does not exists" in {
    val testURL = s"$StoreUsersURL"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }

  /*********************
   * POST /store/users *
   *********************/
  "POST /store/users returns BAD_REQUEST when format is not valid Json" in {
    val testURL = s"$StoreUsersURL"
    val invalidJson ="""
      "email": "hoge@hoge.com",
      "name": " hoge ",
      "name_kana": "ホｹﾞ　",
      "team": "㍿√㌶",
      "hitotalent_id": "1",
      "gendar": "♂",
      "age": 30,
      "token": {},
      "tags": [],
      "events": [],
      "biotops": []
    """
    val requestBody = Json.toJson(invalidJson)
    val response = await(WSClient.url(testURL).post(requestBody))

    response.header(ContentType) mustBe ContentTypeJsonUtf8
    response.status mustBe BAD_REQUEST
  }

  "POST /store/users returns user information with id" in {
    val testURL = s"$StoreUsersURL"
    val validJson ="""{
      "email": "hoge@hoge.com",
      "name": " hoge ",
      "name_kana": "ホｹﾞ　",
      "team": "㍿√㌶",
      "hitotalent_id": "1",
      "gendar": "♂",
      "age": 30,
      "token": {
        "access_token": "63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f",
        "refresh_token": "63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f",
        "access_token_expires_in": "2020-01-01 00:00:00.0",
        "refresh_token_expires_in": "2020-01-01 00:00:00.0"
      },
      "tags": [{"id": 1},{"id": 2},{"id": 3}],
      "events": [{"id": 1},{"id": 2},{"id": 3}],
      "biotops": [{"id": 1},{"id": 2},{"id": 3}],
    }"""
    val requestBody = Json.toJson(validJson)
    val response = await(WSClient.url(testURL).post(requestBody))

    response.header(ContentType) mustBe ContentTypeJsonUtf8
    response.status mustBe OK
    val bodyJson = Json.parse(response.body)
    (bodyJson \ "id").toOption.fold("")(obj => obj.toString) mustBe "1"
    (bodyJson \ "email").toOption.fold("")(obj => obj.toString) mustBe "hoge@hoge.com"
    (bodyJson \ "name").toOption.fold("")(obj => obj.toString) mustBe " hoge "
    (bodyJson \ "name_kana").toOption.fold("")(obj => obj.toString) mustBe "ホｹﾞ　"
    (bodyJson \ "team").toOption.fold("")(obj => obj.toString) mustBe "㍿√㌶"
    (bodyJson \ "hitotalent_id").toOption.fold("")(obj => obj.toString) mustBe "1"
    (bodyJson \ "gendar").toOption.fold("")(obj => obj.toString) mustBe "♂"
    (bodyJson \ "age").toOption.fold("")(obj => obj.toString) mustBe "30"
    (bodyJson \ "team").toOption.fold("")(obj => obj.toString) mustBe "㍿√㌶"
  
    (bodyJson \ "token" \ "access_token").toOption.fold("")(obj => obj.toString) mustBe "63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"
    (bodyJson \ "token" \ "refresh_token").toOption.fold("")(obj => obj.toString) mustBe "63d19ad95b39b88c3a72d08dbe5407a4e0ad8b3f"
    (bodyJson \ "token" \ "access_token_expires_in").toOption.fold("")(obj => obj.toString) mustBe "2020-01-01 00:00:00.0"
    (bodyJson \ "token" \ "refresh_token_expires_in").toOption.fold("")(obj => obj.toString) mustBe "2020-01-01 00:00:00.0"
    (bodyJson \ "tags") mustBe Json.parse(""""tags":[{"id":1},{"id":2},{"id":3}]""")
    (bodyJson \ "events") mustBe Json.parse(""""events":[{"id":1},{"id":2},{"id":3}]""")
    (bodyJson \ "biotops") mustBe Json.parse(""""biotops":[{"id":1},{"id":2},{"id":3}]""")
  }

  /************************
   * GET /store/users (2) *
   ************************/
  "GET /store/users returns all users" in {
    val testURL = s"$StoreUsersURL"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users ignores illegal parameter format" in {
    val testURL = s"$StoreUsersURL?=null&=&?null"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  /*******************************
   * GET /store/users?tagId={id} *
   *******************************/
  "GET /store/users?tagId={id} returns NotFound when anyone does not has argued tag" in {
    val testURL = s"$StoreUsersURL?tagId=999999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND 
  }
  
  "GET /store/users?tagId={id} searches users by tagId" in {
    val testURL = s"$StoreUsersURL?tagId=1"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users?tagId={id} ignores when value is not a number" in {
    val testURL = s"$StoreUsersURL?tagId=1tag"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users?tagId={id} ignores empty value" in {
    val testURL = s"$StoreUsersURL?tagId="
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  /*********************************
   * GET /store/users?eventId={id} *
   *********************************/
  "GET /store/users?eventId={id} returns NotFound when anyone does not has argued event" in {
    val testURL = s"$StoreUsersURL?eventId=999999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }
  
  "GET /store/users?eventId={id} searches users by eventId" in {
    val testURL = s"$StoreUsersURL?eventId=1"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users?eventId={id} ignores when value is not a number" in {
    val testURL = s"$StoreUsersURL?eventId=1event"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users?eventId={id} ignores empty value" in {
    val testURL = s"$StoreUsersURL?eventId="
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
 
  /**********************************
   * GET /store/users?biotopId={id} *
   **********************************/
  "GET /store/users?biotopId={id} returns NotFound when anyone does not has argued biotop" in {
    val testURL = s"$StoreUsersURL?biotopId=999999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }
  
  "GET /store/users?biotopId={id} searches users by biotopId" in {
    val testURL = s"$StoreUsersURL?biotopId=1"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users?biotoptId={id} ignores when value is not a number" in {
    val testURL = s"$StoreUsersURL?biotopId=1biotop"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users?biotopId={id} ignores empty value" in {
    val testURL = s"$StoreUsersURL?biotopId="
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
 
  /*************************
   * GET /store/users/{id} *
   *************************/
  "GET /store/users/{id} returns NotFound when any user does not exists" in {
    val id = 999999
    val testURL = s"$StoreUsersURL/$id"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }

  "GET /store/users/{id} returns stored user information when user exist" in {
    val id = 1
    val testURL = s"$StoreUsersURL/$id"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK 
  }

  "GET /store/users/{id} ignores parameters" in {
    val id = 1
    val testURL = s"$StoreUsersURL/$id?tagId=tag&eventId=イベント&order=?&limit= &offset=null"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }

/*  
  "GET /store/users with order returns ordered result by argued column name" in {
    val testURL = s"$StoreUsersURL?order=name_kana"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users with order returns NOT ordered result when pass not exist column name" in {
    val testURL = s"$StoreUsersURL?order=blahblah"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users with limit returns limited number of users" in {
    val testURL = s"$StoreUsersURL?limit=3"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users with offset returns users excepted top users" in {
    val testURL = s"$StoreUsersURL?offset=3&order=age"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe OK
  }
  
  "GET /store/users with offset returns NOT_FOUND when setted number is greater than number of data" in {
    val testURL = s"$StoreUsersURL?offset=99999"
    val response = await(WSClient.url(testURL).get())

    response.header(ContentType) mustBe ContentTypeJsonUtf8 
    response.status mustBe NOT_FOUND
  }
*/ 
}
