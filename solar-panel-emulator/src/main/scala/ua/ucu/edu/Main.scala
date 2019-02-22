package ua.ucu.edu

import akka.actor._
import ua.ucu.edu.actor.PlantManagerActor
import ua.ucu.edu.actor.PlantManagerActor.RegisterPanel
import ua.ucu.edu.model.Location

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Main extends App {

  implicit val system: ActorSystem = ActorSystem()

  val names = List(
    "Pasadena",
    "Quito",
    "Barselona",
    "Ankara",
    "Lviv",
    "Tokio",
    "Krakow",
    "Paris",
    "Sheffield",
    "Quebec",
    "Chicago",
    "Miami",
    "Mehico",
    "Bogota",
    "Brasilia",
    "Amsterdam",
    "Kyiv",
    "Ternopil",
    "Zhydachiv",
    "Zaporizhia"
  )

  val locations = List(
    Location(29.69106, -95.2091),
    Location(-0.180653, -78.467834),
    Location(41.38879, 2.15899),
    Location(39.933365, 32.859741),
    Location(49.839684, 24.029716),
    Location(39.758602, -104.997437),
    Location(50.061947, 19.936856),
    Location(48.856613, 2.352222),
    Location(53.381130, -1.470085),
    Location(46.777023, -71.262925),
    Location(41.878113, -87.629799),
    Location(25.761681, -80.191788),
    Location(19.432680, -99.134209),
    Location(4.710989, -74.072090),
    Location(-15.826691, -47.921822),
    Location(52.370216, 4.895168),
    Location(50.447731, 30.542721),
    Location(49.553516, 25.594767),
    Location(49.381364, 24.151640),
    Location(47.838799, 35.139568)
  )

  val plantsBuff = ListBuffer[ActorRef]()

  // creating 20 different plants

  for (i <- 0 to 19) {
    plantsBuff += system.actorOf(Props(classOf[PlantManagerActor], names(i), locations(i)), "plant-manager-" + names(i).toLowerCase)
  }

  val plants = plantsBuff.toList

  // creating 50 panels for each plant

  for (i <- 0 to 1)
    for (j <- 1 to 5)
      plants(i) ! RegisterPanel(names(i).toLowerCase + "-" + j.toString + "-" + Random.nextInt(100))

}