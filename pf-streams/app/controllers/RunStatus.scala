package controllers

object RunStatus extends Enumeration {
  type RunStatus = Value
  val UNKNOWN, RUNNING, DONE = Value

  def withNameWithDefault(name: String): Value =
    values.find(_.toString.toLowerCase == name.toLowerCase()).getOrElse(UNKNOWN)
}
