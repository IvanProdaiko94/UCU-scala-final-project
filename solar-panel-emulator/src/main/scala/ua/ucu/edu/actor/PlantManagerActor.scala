package ua.ucu.edu.actor

import akka.actor.{Actor, ActorLogging, ActorRef}
import ua.ucu.edu.model.Location

import scala.collection.mutable

class PlantManagerActor(
  plantId: String,
  location: Location
) extends Actor with ActorLogging {

  val panelToActorRef: mutable.Map[String, ActorRef] = ???

  override def preStart(): Unit = {
    log.info(s"========== Solar Plant Manager starting ===========")
    super.preStart()
  }

  override def receive: Receive = ???
}
