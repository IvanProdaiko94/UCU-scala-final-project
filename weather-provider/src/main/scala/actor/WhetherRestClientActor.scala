package actor

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import ua.ucu.edu.Main.system
import ua.ucu.edu.provider.WeatherProviderApi
import ua.ucu.edu.model.{City, Location, WeatherData}

import scala.concurrent.Future

class WhetherRestClientActor extends Actor with WeatherProviderApi {
  import WhetherRestClientActor._
  import system.dispatcher
  import scala.util.{Failure, Success}
  implicit val actorSystem: ActorSystem = context.system

  val API_URL = sys.env("WEATHER_API_URL")
  val API_KEY = sys.env("WEATHER_API_KEY")

  override def receive: Receive = {
    case InitiateLocationRequest => {
//      weatherAtLocation().onComplete {
//        case Success(s) => {
//          println(s._3)
//          Option(WeatherRecord(Option(location), 0, 0, Option.empty))
//        }
//        case Failure(f) => {
//          println(f.getMessage)
//          Option.empty
//        }
//      }
    }
    case InitiateCityRequest => {
      val c = City(sys.env("WEATHER_LOCATION_CITY"), sys.env("WEATHER_LOCATION_COUNTRY"))
      weatherAtCity(c).onComplete {
        case Success(s) => {
          println(s._3)
          WeatherData(Option.empty, 0, 0, Option(c))
        }
        case Failure(f) => {
          println(f.getMessage)
          Nil
        }
      }
    }
  }

  def weatherAtLocation(location: Location): Future[HttpResponse] = {
    val url = Uri(API_URL)
    Http().singleRequest(
      HttpRequest(
        HttpMethods.GET,
        url.withQuery(Uri.Query(
          ("lat" -> location.latitude.toString),
          ("lon" -> location.longitude.toString),
          ("APPID" -> API_KEY),
        ))
      )
    )
  }

  def weatherAtCity(city: City): Future[HttpResponse] = {
    val url = Uri(API_URL)
    Http().singleRequest(
      HttpRequest(
        HttpMethods.GET,
        url.withQuery(Uri.Query(
          ("q" -> city.toString),
          ("APPID" -> API_KEY),
        ))
      )
    )
  }
}

object WhetherRestClientActor {
  case object InitiateLocationRequest
  case object InitiateCityRequest
}
