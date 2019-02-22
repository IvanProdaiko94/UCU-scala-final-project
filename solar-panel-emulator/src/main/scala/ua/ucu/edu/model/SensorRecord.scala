package ua.ucu.edu.model

/**
  * To be used as a message in device topic
  */
case class SensorRecord(panelId: String, location: String, sensorType: String, measurement: String)