package controllers

import javax.inject._

import java.util.concurrent._

import RunStatus._

@Singleton
class RunCache {
  val statuses = new ConcurrentHashMap[String, RunStatus]()
  val responses = new ConcurrentHashMap[String, String]()
}
