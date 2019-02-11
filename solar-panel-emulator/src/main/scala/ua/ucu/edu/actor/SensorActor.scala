package ua.ucu.edu.actor

import akka.actor.Actor
import ua.ucu.edu.device.SensorApi
import ua.ucu.edu.model.ReadMeasurement

import scala.concurrent.duration._
import scala.language.postfixOps

abstract class SensorActor[T](
  val deviceId: String
) extends Actor {

  context.system.scheduler.schedule(5 second, 5 seconds, self, ReadMeasurement)(
    context.dispatcher, context.parent
  )

  val api: SensorApi[T]

  override def receive: Receive = {
    case ReadMeasurement => sender ! api.readCurrentValue
  }
}
