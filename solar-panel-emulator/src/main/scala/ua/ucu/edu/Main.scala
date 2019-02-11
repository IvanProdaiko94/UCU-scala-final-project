package ua.ucu.edu

import java.util.Properties

import akka.actor._
import akka.cluster._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.slf4j.LoggerFactory
import ua.ucu.edu.actor.PlantManagerActor
import ua.ucu.edu.model.Location

object Main extends App {
  implicit val system: ActorSystem = ActorSystem()
  system.actorOf(Props(classOf[PlantManagerActor], "plant1", Location(0, 0)), "plant1-manager")



  val logger = LoggerFactory.getLogger(getClass)

  val BrokerList = "kafka:9092" //change it to localhost:9092 if not connecting through docker
  val Topic = "sensor-data"

  val props = new Properties()
  props.put("bootstrap.servers", BrokerList)
  props.put("client.id", "solar-panel-#1")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val testMsg = "12.3702"
  logger.info("[Producer] " + testMsg)
  val data = new ProducerRecord[String, String](Topic, testMsg)
  producer.send(data)

  producer.close()

  while(true) {
    Thread.sleep(1000)
    logger.warn("sleep")
  }
}