package com.techmania.scala.classesandobjects

class SimpleScalaClass {
  /**
    * Instance variable it can reassign on every time
    * W.R.T instance
    */
  private var checkSum = 0;

  def max(x: Int, y: Int): Int = if (x.compareTo(y) == 1) return x else y

  def findCheckSum(input: Int): Int = {
    assignIfAbsent(input)
    return ~(checkSum & 0xFF) + 1
  }

  private def assignIfAbsent(value: Int): Unit = {
    if (checkSum == 0) checkSum = value
  }

  def checkSum(value: Int): Int = {
    assignIfAbsent(value);
    ~(checkSum & 0xFF) + 1
  }

  def statement(a: Int, b: Int, isOld: Boolean): Int = {
    if (isOld) {
      a
      +b
    } else {
      a + b
    }
  }
}
