package ua.ucu.edu.model

sealed trait Measurement

case object Temperature extends Measurement
case object Pressure extends Measurement
case object Humidity extends Measurement
