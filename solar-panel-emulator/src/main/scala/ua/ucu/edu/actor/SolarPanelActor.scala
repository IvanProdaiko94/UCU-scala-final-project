package ua.ucu.edu.actor

import akka.actor.{Actor, ActorRef}

import scala.collection.mutable

class SolarPanelActor extends Actor {

  val solarPanelId

  val deviceToActorRef: mutable.Map[String, ActorRef] = ???

  override def receive: Receive = {

  }
}
