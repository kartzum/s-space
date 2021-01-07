package controllers

import java.util.UUID

class RunClientWithWork(val runCache: RunCache) extends RunClient {
  override def sendRun(key: String, run: Run): Unit = {
    val newKey = UUID.randomUUID().toString
    val newRun = Run(newKey, RunType.RESPONSE, key, run.body + "_calculated")
    runCache.statuses.replace(key, RunStatus.DONE)
    runCache.responses.replace(key, newRun.body)
  }
}
