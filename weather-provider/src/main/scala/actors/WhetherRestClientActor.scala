package actors

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import ua.ucu.edu.Main.system

class WhetherRestClientActor extends Actor {
  import WhetherRestClientActor._
  import system.dispatcher

  import scala.util.{Failure, Success}

  implicit val actorSystem: ActorSystem = context.system

  val API_URL = sys.env("WEATHER_API_KEY")
  val API_KEY = sys.env("WEATHER_TOPIC_NAME")

  override def receive: Receive = {
    case InitiateRequest => {
      println("LOG")
    }
//      Http().singleRequest(HttpRequest(uri = API_URL)).onComplete {
//        case Success(s) => println(s._3)
//        case Failure(f) => println(f.getMessage)
//      }
  }
}

object WhetherRestClientActor {
  case object InitiateRequest
}
