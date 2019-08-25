package com.techmania.util

import com.techmania.models.Point

object MathUtil {
  def distance(x: Point, y: Point): Double = {
    return Math.sqrt(
      (x.getX().-(y.getX())).*
      (x.getX().-(y.getX())).+
      (x.getY().-(y.getY())).*
      (x.getY().-(y.getY())));
  }
}
