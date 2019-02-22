package ua.ucu.edu.actor

import akka.actor.{Actor, ActorLogging, Props}
import ua.ucu.edu.device.{SensorApi, SensorGenerator}
import ua.ucu.edu.model.{Measurement, ReadMeasurement, RespondMeasurement}

import scala.language.postfixOps

object SensorActor {

  def props(deviceId: String, sensorType: Measurement): Props = Props(new SensorActor(deviceId, sensorType))

}



class SensorActor(val deviceId: String, sensorType: Measurement) extends Actor with ActorLogging{

  val api: SensorApi = new SensorGenerator


  override def preStart(): Unit = {
    log.info("[Started] Sensor actor {}:{}", deviceId, sensorType.toString)
  }

  override def postStop(): Unit = {
    log.info("[Stopped] Sensor actor {}:{}", deviceId, sensorType.toString)
  }

  override def receive: Receive = {
    case ReadMeasurement =>
      sender ! RespondMeasurement(
        deviceId,
        sensorType.toString,
        api.readCurrentValue(sensorType)
      )
  }

}
