package controllers

import com.google.inject.ImplementedBy

@ImplementedBy(classOf[RunClientImpl])
trait RunClient {
  def sendRun(key: String, run: Run): Unit
}
