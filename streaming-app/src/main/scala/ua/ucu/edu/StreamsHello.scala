package ua.ucu.edu

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}

object StreamsHello extends App {

  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test_app_hello")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092")
  props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Long.box(5 * 1000))
  props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, Long.box(0))

  import Serdes._

  val builder = new StreamsBuilder

  // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
  // represent lines of text (for the sake of this example, we ignore whatever may be stored
  // in the message keys).
  val textLines = builder.stream[String, String]("test_words")

//  val wordCounts = textLines.flatMapValues // Split each text line, by whitespace, into words.  The text lines are the message
//  // values, i.e. we can ignore whatever data is in the message keys and thus invoke
//  // `flatMapValues` instead of the more generic `flatMap`.
//  ((value) => util.Arrays.asList(value.toLowerCase.split("\\W+"))).groupBy // We use `groupBy` to ensure the words are available as message keys
//  ((key, value) => value).count // Count the occurrences of each word (message key).
//
//
//  // Convert the `KTable<String, Long>` into a `KStream<String, Long>` and write to the output topic.
//  wordCounts.to("streams-wordcount-output", Produced.`with`(stringSerde, longSerde))

  textLines.to("test_words_out")

  val streams = new KafkaStreams(builder.build(), props)
  streams.cleanUp()
  streams.start()

  sys.addShutdownHook {
    streams.close(10, TimeUnit.SECONDS)
  }
}
