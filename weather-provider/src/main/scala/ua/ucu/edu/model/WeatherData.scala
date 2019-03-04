package ua.ucu.edu.model

/**
  * To be used as a record in kafka topic
  */
case class WeatherData(location: Location, temperature: Number, humidity: Number, city: City)
