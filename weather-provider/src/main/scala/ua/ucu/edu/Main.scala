package ua.ucu.edu

import actor.WhetherRestClientActor
import akka.actor.{ActorSystem, Props}
import org.slf4j.LoggerFactory

import scala.concurrent.duration
import scala.language.postfixOps

import ua.ucu.edu.model.Location

object Main extends App {
  val locString = "Pasadena,-95.2091,29.69106\nQuito,-78.467834,-0.180653\nBarselona,2.15899,41.38879\nAnkara,32.859741,39.933365\nLviv,24.029716,49.839684\nTokio,-104.997437,39.758602\nKrakow,19.936856,50.061947\nParis,2.352222,48.856613\nSheffield,-1.470085,53.38113\nQuebec,-71.262925,46.777023\nChicago,-87.629799,41.878113\nMiami,-80.191788,25.761681\nMehico,-99.134209,19.43268\nBogota,-74.07209,4.710989\nBrasilia,-47.921822,-15.826691\nAmsterdam,4.895168,52.370216\nKyiv,30.542721,50.447731\nTernopil,25.594767,49.553516\nZhydachiv,24.15164,49.381364\nZaporizhia,35.139568,47.838799"
  var locations: List[Location] = List()
  for (line <- locString.split("\n")) {
    val cols = line.split(",").map(_.trim)
    locations = locations :+ Location(cols(0), cols(1).toFloat, cols(2).toFloat)
  }

  WhetherRestClientActor.Locations.set(locations)

  val logger = LoggerFactory.getLogger(getClass)
  logger.info("======== Weather Provider App Init ========")

  val system = ActorSystem()
  import system.dispatcher
  import duration._

  val actor = system.actorOf(Props[WhetherRestClientActor], "WhetherRestClientActor")

  system.scheduler.schedule(10 seconds, 10 minutes, actor, WhetherRestClientActor.InitiateLocationRequest)
}
