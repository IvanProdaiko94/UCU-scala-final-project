package ua.ucu.edu.provider

import ua.ucu.edu.model._
import akka.http.scaladsl.model.HttpResponse
import ua.ucu.edu.model.{City, Location}

import scala.concurrent.Future

trait WeatherProviderApi {
  def weatherAtLocation(location: Location): Future[HttpResponse]

  def weatherAtCity(city: City): Future[HttpResponse]
}
