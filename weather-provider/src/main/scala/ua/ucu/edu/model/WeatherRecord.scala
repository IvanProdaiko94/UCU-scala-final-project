
package ua.ucu.edu.model
import ua.ucu.edu._

case class WeatherRecord(location: Option[Location], temperature: Number, humidity: Number, city: Option[City])
