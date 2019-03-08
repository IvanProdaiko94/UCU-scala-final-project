package ua.ucu.edu

import actor.WhetherRestClientActor
import akka.actor.{ActorSystem, Props}
import org.slf4j.LoggerFactory

import scala.concurrent.duration
import scala.language.postfixOps
import scala.io.Source.fromFile

import ua.ucu.edu.model.Location

object Main extends App {
  val bufferedSource = fromFile(System.getenv("LOCATIONS_FILE_PATH"))
  var locations: List[Location] = List()
  for (line <- bufferedSource.getLines) {
    val cols = line.split(",").map(_.trim)
    locations = locations :+ Location(cols(0), cols(1).toFloat, cols(2).toFloat)
  }
  bufferedSource.close

  WhetherRestClientActor.Locations.set(locations)

  val logger = LoggerFactory.getLogger(getClass)
  logger.info("======== Weather Provider App Init ========")

  val system = ActorSystem()
  import system.dispatcher
  import duration._

  val actor = system.actorOf(Props[WhetherRestClientActor], "WhetherRestClientActor")

  system.scheduler.schedule(3 seconds, 1 seconds, actor, WhetherRestClientActor.InitiateLocationRequest)
}
