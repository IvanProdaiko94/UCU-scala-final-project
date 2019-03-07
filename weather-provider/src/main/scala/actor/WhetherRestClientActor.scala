package actor

import java.util.Properties

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import ua.ucu.edu.Main.system
import ua.ucu.edu.provider.WeatherProviderApi
import ua.ucu.edu.model.{City, Location}

import scala.concurrent.Future

object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
  val WeatherTopic = "WEATHER_TOPIC_NAME"
}

class WhetherRestClientActor extends Actor with WeatherProviderApi with ActorLogging  {
  import WhetherRestClientActor._
  import system.dispatcher
  import scala.util.{Failure, Success}
  implicit val actorSystem: ActorSystem = context.system

  val BrokerList: String = System.getenv(Config.KafkaBrokers)
  val topic = System.getenv(Config.WeatherTopic)
  val props = new Properties()

  log.info("[Kafka] Started topic: {}", topic)

  props.put("bootstrap.servers", BrokerList)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val API_URL = System.getenv("WEATHER_API_URL")
  val API_KEY = System.getenv("WEATHER_API_KEY")

  override def receive: Receive = {
    case InitiateLocationRequest => {
      weatherAtLocation(Location(
        System.getenv("WEATHER_LOCATION_LON").toFloat,
        System.getenv("WEATHER_LOCATION_LAT").toFloat,
      )).onComplete {
        case Success(s) => {
          log.info("[Kafka] Stream data\n: {}", s._3.toString)
          val data = new ProducerRecord[String, String](topic, s._3.toString)
          producer.send(data)
        }
        case Failure(f) => {
          println(f.getMessage)
        }
      }
    }
    case InitiateCityRequest => {
      val c = City(System.getenv("WEATHER_LOCATION_CITY"), System.getenv("WEATHER_LOCATION_COUNTRY"))
      weatherAtCity(c).onComplete {
        case Success(s) => {
          log.info("[Kafka] Stream data\n: {}", s._3.toString)
          val data = new ProducerRecord[String, String](topic, s._3.toString)
          producer.send(data)
        }
        case Failure(f) => {
          println(f.getMessage)
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

  override def postStop(): Unit = {
    log.info("[Stopped] weather data producer")
    producer.close()
  }
}

object WhetherRestClientActor {
  case object InitiateLocationRequest
  case object InitiateCityRequest
}
