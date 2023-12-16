import controller.model.AccountDetail
import controller.model.TransactionDetail
import controller.model.UserDetail
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.model.AccountStatus
import FunctionalSpec.Fixture

/**
 * Functional tests start a Play application internally, available
 * as `app`.
 */
class FunctionalSpec extends PlaySpec with GuiceOneAppPerSuite with Fixture {

  "Account Route" should {

    "send 404 on empty accountId" in {
      route(app, FakeRequest(GET, "/account")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 404 on non-existing accountId" in {
      route(app, FakeRequest(GET, "/account/666")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 200 on valid accountId" in {
      route(app, FakeRequest(GET, "/account/100")).map(status(_)) mustBe Some(OK)
    }

    "return account details" in {
      val response = route(app, FakeRequest(GET, "/account/200")).get
      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      val json = contentAsJson(response)
      json.as[AccountDetail] mustBe expectedAccount
    }
  }

  "Transaction Routes" should {

    "send 415 on invalid payload" in {
      route(app, FakeRequest(POST, "/transaction")).map(status(_)) mustBe Some(UNSUPPORTED_MEDIA_TYPE)
    }

    "send 400 on a bad request (empty payload)" in {
      val body = Json.obj()
      route(app, FakeRequest(POST, "/transaction").withJsonBody(body)).map(status(_)) mustBe Some(BAD_REQUEST)
    }

    "send 400 on a bad request (missing field)" in {
      val body = Json.obj(("accountId", "1234"), ("amount", 10.99))
      route(app, FakeRequest(POST, "/transaction").withJsonBody(body)).map(status(_)) mustBe Some(BAD_REQUEST)
    }

    "send 422 due to insufficient funds" in {
      val body = Json.obj(("accountId", "100"), ("amount", -1000000.0), ("description", "CASHIER:CASH"))
      route(app, FakeRequest(POST, "/transaction").withJsonBody(body)).map(status(_)) mustBe Some(
        UNPROCESSABLE_ENTITY
      )
    }

    "send 201 on sufficient funds" in {
      val body = Json.obj(("accountId", "200"), ("amount", -10.0), ("description", "CASHIER:CASH"))
      route(app, FakeRequest(POST, "/transaction").withJsonBody(body)).map(status(_)) mustBe Some(CREATED)
    }
  }

  "Transaction History Route" should {

    "send 404 on invalid request" in {
      route(app, FakeRequest(GET, "/transaction/history")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 404 on non-existing accountId" in {
      route(app, FakeRequest(GET, "/transaction/history/666")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 200 on existing accountId" in {
      route(app, FakeRequest(GET, "/transaction/history/100")).map(status(_)) mustBe Some(OK)
    }

    "return 2 transaction details" in {
      val response = route(app, FakeRequest(GET, "/transaction/history/100")).get
      status(response) mustBe OK
      contentType(response) mustBe Some("application/json")
      val json = contentAsJson(response)
      json.as[JsArray].value.size mustBe 2
      json.as[List[TransactionDetail]].map(_.transactionId) mustBe List(expectedId2, expectedId1)
      json.as[List[TransactionDetail]].map(_.amount) mustBe List(expectedAmount2, expectedAmount1)
    }
  }
}

object FunctionalSpec {

  trait Fixture {
    val expectedId1     = 1
    val expectedAmount1 = -12.5
    val expectedId2     = 2
    val expectedAmount2 = 100
    val expectedUser = UserDetail(
      userId = 10,
      firstName = "Charon",
      lastName = "Manager"
    )
    val expectedAccount = AccountDetail(
      accountId = "200",
      balance = 720.0,
      status = AccountStatus.ACTIVE,
      user = expectedUser
    )
  }
}
