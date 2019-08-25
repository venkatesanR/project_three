package com.techmania.models

import com.techmania.util.MathUtil

class Point(pointX: Double, pointY: Double) {
  def getX(): Double = {
    return pointX;
  }

  def getY(): Double = {
    return pointX;
  }

  def distance(anotherPoint: Point): Double = {
    return MathUtil.distance(this, anotherPoint);
  }
}
