package ua.ucu.edu.device

trait SensorApi[T] {

  def readCurrentValue: T
}
