package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsString, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class RunControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  "RunController" should {
    "runs" in {
      val runCache = new RunCache()
      val runClientWithWork = new RunClientWithWork(runCache)
      val controller = new RunController(stubControllerComponents(), runCache, runClientWithWork)

      val body = Json.parse("\"body\"")
      val result: Future[Result] = controller.runs().apply(FakeRequest(POST, "/runs").withJsonBody(body))
      status(result) mustBe OK
      val key = contentAsJson(result).as[JsString].value
      key must not equal ("")

      var runStatus = RunStatus.UNKNOWN
      while (runStatus != RunStatus.DONE) {
        val runStatusResult = controller.getRunStatus(key).apply(FakeRequest(GET, "/runs/" + key + "/status"))
        val runStatusResponse = contentAsJson(runStatusResult).as[JsString].value
        runStatus = RunStatus.withNameWithDefault(runStatusResponse)
      }

      val responseResult = controller.getRunResponse(key).apply(FakeRequest(GET, "/runs/" + key))
      val response = contentAsJson(responseResult).as[JsString].value
      response must equal("body_calculated")
    }
  }
}
