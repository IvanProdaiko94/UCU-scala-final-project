package ua.ucu.edu.device

import ua.ucu.edu.model.Measurement

trait SensorApi {
  def readCurrentValue(sensorType: Measurement): String
}
