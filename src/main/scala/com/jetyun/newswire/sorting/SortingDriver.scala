package com.jetyun.newswire.sorting

import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * @author Administrator
 */
object SortingDriver {
  def sort(data: RDD[(Int, Array[Double])]): RDD[(Int, Double)] = {
    val result = data.map { label =>
      val array = label._2
      var weight = 0.0
      for (i <- 0 until array.length) {
        weight += array(i) * SortDimen.dimen(i)
      }
      (label._1, weight)
    }.sortBy(f => f._2, false)
    result
  }
}