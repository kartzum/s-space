package controllers

import controllers.RunType.RunType

case class Run(key: String, runType: RunType, responseKey: String, body: String)
