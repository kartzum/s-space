package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

import java.util._

class RunController @Inject()(
                               cc: ControllerComponents,
                               runCache: RunCache,
                               runClient: RunClient,
                             ) extends AbstractController(cc) {

  def runs(): Action[AnyContent] = Action { implicit request =>
    val body = request.body.asJson.get.as[JsString]
    val key = UUID.randomUUID().toString
    runCache.statuses.put(key, RunStatus.RUNNING)
    runCache.responses.put(key, "")
    runClient.sendRun(key, Run(key, RunType.REQUEST, "", body.value))
    Ok(Json.toJson(key))
  }

  def getRunStatus(key: String): Action[AnyContent] = Action { implicit request =>
    val status = runCache.statuses.getOrDefault(key, RunStatus.UNKNOWN)
    Ok(Json.toJson(status))
  }

  def getRunResponse(key: String): Action[AnyContent] = Action { implicit request =>
    val response = runCache.responses.getOrDefault(key, "")
    Ok(Json.toJson(response))
  }
}
