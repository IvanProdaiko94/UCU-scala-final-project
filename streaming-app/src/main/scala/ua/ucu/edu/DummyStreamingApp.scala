package ua.ucu.edu

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.slf4j.LoggerFactory

// dummy app for testing purposes
object DummyStreamingApp extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming_app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(Config.KafkaBrokers))
  props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Long.box(5 * 1000))
  props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, Long.box(0))

  import Serdes._

  val builder = new StreamsBuilder

  val sensor_data_stream = builder.stream[String, String]("nonames-sensor-data").map(
    (_, value) => (value.split('-')(0), value.split('-').slice(1, 999).mkString("-")))
  val weather_data_stream = builder.stream[String, String]("nonames-weather-data")  // to resolve
  val main_stream = sensor_data_stream  // .join(weather_data_stream,
//    (sensorValue, weatherValue) -> "left=" + leftValue + ", right=" + rightValue, /* ValueJoiner */
//    JoinWindows.of(TimeUnit.MINUTES.toMillis(5)),
//    Serdes.String(), /* key */
//    Serdes.Long(),   /* left value */
//    Serdes.Double()  /* right value */
//  )

  main_stream.foreach { (k, v) =>
    logger.info(s"test record processed $k->$v")
  }

  main_stream.to("test_topic_out")

  val streams = new KafkaStreams(builder.build(), props)
  streams.cleanUp()
  streams.start()

  sys.addShutdownHook {
    streams.close(10, TimeUnit.SECONDS)
  }

  object Config {
    val KafkaBrokers = "KAFKA_BROKERS"
  }
}
