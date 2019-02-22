package ua.ucu.edu.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import ua.ucu.edu.actor.SolarPanelActor.RegisterSensors
import ua.ucu.edu.model.Location

import scala.collection.mutable

/**
  * This actor manages solar plant, holds a list of panels and knows about its location
  * TODO: the main purpose right now to initialize panel actors
  */

object PlantManagerActor {

  final case class RegisterPanel(panelId: String)
  case object PanelRegistered

}

class PlantManagerActor(plantName: String, location: Location) extends Actor with ActorLogging {

  import PlantManagerActor._

  // TODO: populate a list of panels on this plant

  lazy val panelToActor = mutable.Map.empty[String, ActorRef]
  lazy val actorToPanel = mutable.Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("[Started] Solar Plant Manager at {}", plantName)

  override def postStop(): Unit = log.info("[Stopped] Solar Plant Manager at {}", plantName)


  override def receive: Receive = {
    case registerMassage @ RegisterPanel(panelId) => {

      panelToActor.get(panelId) match {

        case Some(ref) =>
          log.info("[Warning] Panel : {} already exists", panelId)

        case None =>
          log.info("[Registered] Panel : {}", panelId)
          val panel = context.actorOf(SolarPanelActor.props(panelId, location), "panel:" + panelId)
          context.watch(panel)
          panel forward RegisterSensors
          panelToActor += panelId -> panel
          actorToPanel += panel -> panelId
      }
    }

    case Terminated(panel) => {
      val panelId = actorToPanel(panel)
      log.info("[Terminated] Panel : {}", panelId)
      actorToPanel -= panel
      panelToActor -= panelId
    }
  }
}
