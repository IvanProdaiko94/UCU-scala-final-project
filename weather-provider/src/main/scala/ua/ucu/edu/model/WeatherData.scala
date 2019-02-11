package ua.ucu.edu.model

import ua.ucu.edu._

/**
  * To be used as a record in kafka topic
  */
case class WeatherData(location: Option[Location], temperature: Number, humidity: Number, city: Option[City])
