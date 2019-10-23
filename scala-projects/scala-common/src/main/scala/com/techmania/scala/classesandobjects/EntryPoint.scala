package com.techmania.scala.classesandobjects

object EntryPoint extends {
  def main(args: Array[String]): Unit = {
    val compute = new SimpleScalaClass
    println(compute.checkSum(2))
    println(compute.statement(3, 4, true))
    println(compute.statement(3, 4, false))
    println(ScalaSingleton.calculate("Every value is an object."))
  }
}

