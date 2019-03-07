package ua.ucu.edu

import actor.WhetherRestClientActor
import akka.actor.{ActorSystem, Props}
import org.slf4j.LoggerFactory

import scala.concurrent.duration
import scala.language.postfixOps

object Main extends App {

  val logger = LoggerFactory.getLogger(getClass)

  logger.info("======== Weather Provider App Init ========")

  val system = ActorSystem()
  import system.dispatcher

  import duration._

  val actor = system.actorOf(Props[WhetherRestClientActor], "WhetherRestClientActor")

  system.scheduler.schedule(3 seconds, 1 seconds, actor, WhetherRestClientActor.InitiateCityRequest)
}
