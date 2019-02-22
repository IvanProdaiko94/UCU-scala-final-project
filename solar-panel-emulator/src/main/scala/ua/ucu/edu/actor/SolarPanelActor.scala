package ua.ucu.edu.actor

import java.util.Properties

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import ua.ucu.edu.model._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Keeps a list of device sensor actors, schedules sensor reads and pushes updates into sensor data topic
  */

object SolarPanelActor {
  def props(panelId: String, location: Location): Props = Props(new SolarPanelActor(panelId, location))

  case object RegisterSensors
  case object SensorsRegistered
}


object Config {
  val KafkaBrokers = "KAFKA_BROKERS"
}


class SolarPanelActor(val panelId: String, location: Location) extends Actor with ActorLogging {

  import SolarPanelActor._

  val BrokerList: String = System.getenv(Config.KafkaBrokers)
  val topic = "sensor-data"
  val props = new Properties()

  log.info("[Kafka] Started for panel {}", panelId)

  props.put("bootstrap.servers", BrokerList)
  props.put("client.id", "Solar panel:" + panelId)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  // DONE: initialize device actors

  val sensorToActor = mutable.Map.empty[String, ActorRef]
  val actorToSensor = mutable.Map.empty[ActorRef, String]

  override def preStart(): Unit = {
    log.info("[Started] Solar panel : {}", panelId)
    // DONE: schedule measurement reads
    context.system.scheduler.schedule(5 second, 5 seconds, self, ReadMeasurement)(context.dispatcher, self)
  }

  override def postStop(): Unit = {
    log.info("[Stopped] Solar panel : {}", panelId)
    producer.close()
  }


  override def receive: Receive = {

    // DONE: handle measurement respond and push it to kafka

    case ReadMeasurement => {
      for (child <- actorToSensor.keySet) {
        child ! ReadMeasurement
      }
    }

    case RespondMeasurement(deviceId, sensorType, value) => {
      log.info("[Push] Sending message from panel {} to topic {}", panelId, topic)

      val message =
        deviceId + "|" + location.latitude.toString + "|" + location.longitude.toString + "|" + sensorType + "|" + value

      val data = new ProducerRecord[String, String](topic, message)
      producer.send(data)
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
