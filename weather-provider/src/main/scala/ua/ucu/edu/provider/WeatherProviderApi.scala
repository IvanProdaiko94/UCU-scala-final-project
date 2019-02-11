package ua.ucu.edu.provider

import ua.ucu.edu._
import ua.ucu.edu.model.Location

trait WeatherProviderApi {

  def weatherAtLocation(location: Location)
}
