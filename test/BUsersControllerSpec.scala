import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
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
  val WSClient = app.injector.instanceOf[WSClient]
  val Localhost = s"http://localhost:$port"
  
  "GET /store/users returns NotFound when no user exists" in {
    val testURL = s"$Localhost/store/users"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe NOT_FOUND
  }

  "GET /store/users ignores empty parameters" in {
    val testURL = s"$Localhost/store/users?tagId=&eventId=&order=&limit=&offset="
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users ignores when parameter value is not number" in {
    val testURL = s"$Localhost/store/users?tagId=tag&eventId=イベント&order=?&limit= &offset=null"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users with tagId searches users by tagId" in {
    val testURL = s"$Localhost/store/users?tagId=1"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users with eventId searches users by eventId" in {
    val testURL = s"$Localhost/store/users?eventId=1"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users with order returns ordered result by argued column name" in {
    val testURL = s"$Localhost/store/users?order=name_kana"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users with order returns NOT ordered result when pass not exist column name" in {
    val testURL = s"$Localhost/store/users?order=blahblah"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users with limit returns limited number of users" in {
    val testURL = s"$Localhost/store/users?limit=3"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users with offset returns users excepted top users" in {
    val testURL = s"$Localhost/store/users?offset=3&order=age"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe OK
  }
  
  "GET /store/users with offset returns NOT_FOUND when setted number is greater than number of data" in {
    val testURL = s"$Localhost/store/users?offset=99999"
    val response = await(WSClient.url(testURL).get())

    response.status mustBe NOT_FOUND
  }
  
}
