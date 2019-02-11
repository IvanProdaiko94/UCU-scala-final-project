package ua.ucu.edu

import java.util.Properties

import actors.WhetherRestClientActor
import akka.actor.{ActorSystem, Props}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
//import org.slf4j.LoggerFactory

import scala.concurrent.duration
import scala.language.postfixOps

object Main extends App {

//  val logger = LoggerFactory.getLogger(getClass)
//
//  logger.info("======== Weather Provider App Init ========")

  val system = ActorSystem()
  import system.dispatcher

  import duration._

  val actor = system.actorOf(Props[WhetherRestClientActor], "WhetherRestClientActor")

  system.scheduler.schedule(5 seconds, 1 seconds, actor, WhetherRestClientActor.InitiateRequest)

//  val BrokerList = "kafka:9092" //change it to localhost:9092 if not connecting through docker
//  val Topic = sys.env("WEATHER_API_URL")
//
//  val props = new Properties()
//  props.put("bootstrap.servers", BrokerList)
//  props.put("client.id", "solar-panel-#1")
//  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
//
//  val producer = new KafkaProducer[String, String](props)
//
//  val testMsg = "hot"
//  logger.info("[Weather Data Producer] " + testMsg)
//  val data = new ProducerRecord[String, String](Topic, testMsg)
//  producer.send(data)
//
//  producer.close()
//
//  while(true) {
//    Thread.sleep(1000)
//    logger.warn("sleep")
//  }
}
