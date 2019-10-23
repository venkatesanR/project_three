package com.techmania.scala.classesandobjects

import scala.collection.mutable.Map

class ScalaSingleton {
  private var sum = 0

  def add(b: Byte) {
    sum += b
  }

  def samplke(): Unit = {

  }

  def checksum(): Int = ~(sum & 0xFF) + 1
}

/** *
  * This object is association to scala class ScalaSingleton
  * a.k.a CompanionObject
  */
object ScalaSingleton {
  private val cache = Map[String, Int]()

  def calculate(s: String): Int =
    if (cache.contains(s))
      return cache(s)
    else {
      val acc = new ScalaSingleton
      for (c <- s)
        acc.add(c.toByte)
      val cs = acc.checksum()
      cache += (s -> cs)
      return cs
    }
}