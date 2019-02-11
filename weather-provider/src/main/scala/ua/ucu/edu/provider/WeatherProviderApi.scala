package ua.ucu.edu.provider

import ua.ucu.edu._
import ua.ucu.edu.model.{Location, City}

trait WeatherProviderApi {
  def weatherAtLocation(location: Location)

  def weatherAtCity(city: City)
}
