package ua.ucu.edu

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.slf4j.LoggerFactory

object Main extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streaming_app")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv(Config.KafkaBrokers))
  props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Long.box(5 * 1000))
  props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, Long.box(0))

  import Serdes._

  val builder:StreamsBuilder = new StreamsBuilder

  val sensor_data_stream = builder.stream[String, String]("nonames-sensor-data").map(
    (_, value) => (value.split('-')(0), value.split('-').slice(1, 999).mkString("-")))
  //  val weather_data_stream = builder.stream[String, String]("nonames-weather-data").map(
  //    (key, value) => (key.toLowerCase(), value)
  //  )
  val weather_data_table = builder.table[String, String]("nonames-weather-data").mapValues(
    value => value.split("json,")(1).dropRight(1))
  // HttpEntity.Strict(application/json,{"coord":{"lon":-0.18,"lat":-78.47},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"base":"stations","main":{"temp":248.874,"pressure":1006.14,"humidity":67,"temp_min":248.874,"temp_max":248.874,"sea_level":1006.14,"grnd_level":722.68},"wind":{"speed":8.91,"deg":9.00165},"clouds":{"all":44},"dt":1552076514,"sys":{"message":0.0033,"sunrise":1552018682,"sunset":1552075135},"id":0,"name":"","cod":200})
  val main_stream = sensor_data_stream.join(weather_data_table)(
    (sensorValue, weatherValue) => "sensorValue=" + sensorValue + ", weatherValue=" + weatherValue /* ValueJoiner */
  )

//  sensor_data_stream.foreach { (k, v) =>
//    logger.info(s"sensor record processed $k->$v")
//  }

//  weather_data_table.toStream.foreach { (k, v) =>
//    logger.info(s"weather record processed $k->$v")
//  }

  main_stream.foreach { (k, v) =>
    logger.info(s"main record $k->$v")
  }

  main_stream.to("nonames-results")

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
