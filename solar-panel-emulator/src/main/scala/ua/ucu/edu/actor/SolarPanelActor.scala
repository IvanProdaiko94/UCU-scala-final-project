package ua.ucu.edu.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import ua.ucu.edu.model._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps




/**
  * Keeps a list of device sensor actors, schedules sensor reads and pushes updates into sensor data topic
  */


object SolarPanelActor {

  def props(panelId: String): Props = Props(new SolarPanelActor(panelId))

  case object RegisterSensors
  case object SensorsRegistered
}



class SolarPanelActor(val panelId: String) extends Actor with ActorLogging {

  import SolarPanelActor._

  // todo - initialize device actors

  val sensorToActor = mutable.Map.empty[String, ActorRef]
  val actorToSensor = mutable.Map.empty[ActorRef, String]



  override def preStart(): Unit = {
    log.info("[Started] Solar panel : {}", panelId)

    // todo - schedule measurement reads

    context.system.scheduler.schedule(5 second, 5 seconds, self, ReadMeasurement)(context.dispatcher, self)
  }

  override def postStop(): Unit = {
    log.info("[Stopped] Solar panel : {}", panelId)
  }


  override def receive: Receive = {
    // case _ => ???
    // todo handle measurement respond and push it to kafka

    case RespondMeasurement => {

    }


    case RegisterSensors => {
      val tSensor = context.actorOf(SensorActor.props(panelId, Temperature), panelId + ":" + Temperature.toString)
      val pSensor = context.actorOf(SensorActor.props(panelId, Pressure), panelId + ":" + Pressure.toString)
      val hSensor = context.actorOf(SensorActor.props(panelId, Humidity), panelId + ":" + Humidity.toString)

      context.watch(tSensor)
      context.watch(pSensor)
      context.watch(hSensor)

      sensorToActor += panelId + ":" + Temperature.toString -> tSensor
      sensorToActor += panelId + ":" + Pressure.toString -> pSensor
      sensorToActor += panelId + ":" + Humidity.toString -> hSensor

      actorToSensor += tSensor -> (panelId + ":" + Temperature.toString)
      actorToSensor += pSensor -> (panelId + ":" + Pressure.toString)
      actorToSensor += hSensor -> (panelId + ":" + Humidity.toString)

    }

    case Terminated(sensor) => {
      val sensorId = actorToSensor(sensor)
      log.info("[Terminated] Sensor: {}", panelId)
      sensorToActor -= sensorId
      actorToSensor -= sensor
    }
  }
}
