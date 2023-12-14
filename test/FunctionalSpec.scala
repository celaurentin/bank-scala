import FunctionalSpec.expectedResponse
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * Functional tests start a Play application internally, available
 * as `app`.
 */
class FunctionalSpec extends PlaySpec with GuiceOneAppPerSuite {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/arepas")).map(status(_)) mustBe Some(NOT_FOUND)
    }

    "send 400 on a bad request (missing request params)" in  {
      route(app, FakeRequest(GET, "/loan/report")).map(status(_)) mustBe Some(BAD_REQUEST)
    }

  }

  "LendingController" should {
    "render report data" in {
      val report = route(app, FakeRequest(GET, "/loan/report?reportType=Amount&groupingKey=Grade")).get

      status(report) mustBe Status.OK
      contentType(report) mustBe Some("application/json")
      contentAsString(report) must include (expectedResponse)
    }

  }
}

object FunctionalSpec {
  val expectedResponse =
    """{"title":"Total Loans by Grade","xLabel":"Grades","yLabel":"Total Loans - Millions","data":[{"label":"B","value":"555179425"},{"label":"C","value":"504409350"},{"label":"A","value":"364672175"},{"label":"D","value":"278252525"},{"label":"E","value":"79555175"},{"label":"F","value":"24585600"},{"label":"G","value":"10512425"}]}"""

}
