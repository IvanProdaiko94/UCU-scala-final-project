package ua.ucu.edu.model

case class City(name: String, countryCode: String) {
  override def toString(): String = {
    var result = s"$name"
    if (countryCode != "") {
      result = result+ s",$countryCode"
    }
    result
  }
}
