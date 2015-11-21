package com.jetyun.newswire

import org.apache.spark.SparkContext

/**
 * @author Administrator
 */
trait TestBase {
  val sc = new SparkContext("local[2]","test")
}