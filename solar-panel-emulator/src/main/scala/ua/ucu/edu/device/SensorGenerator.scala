package ua.ucu.edu.device


import scala.util.Random
import ua.ucu.edu.model._

class SensorGenerator extends SensorApi {

  // DONE:  generate sensor data

  override def readCurrentValue(sensorType: Measurement): String = {
    sensorType match {
      case Temperature => (-30 + Random.nextInt(60)).toString
      case Pressure => (900 + Random.nextInt(200)).toString
      case Humidity => Random.nextInt(100).toString
    }
  }
}
