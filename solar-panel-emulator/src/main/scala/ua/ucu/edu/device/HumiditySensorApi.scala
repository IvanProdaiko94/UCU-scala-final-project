package ua.ucu.edu.device

import scala.util.Random

class HumiditySensorApi extends SensorApi[Long] {
  override def readCurrentValue: Long = Random.nextLong
}
