package controllers

import javax.inject._

@Singleton
class RunClientImpl extends RunClient {
  override def sendRun(key: String, run: Run): Unit = {}
}
