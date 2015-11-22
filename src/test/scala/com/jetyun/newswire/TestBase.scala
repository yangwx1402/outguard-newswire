package com.jetyun.newswire

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkConf

/**
 * @author Administrator
 */
trait TestBase {
  val conf = new SparkConf
  conf.setMaster("local[2]")
  conf.setAppName("test")
  conf.set("spark.driver.maxResultSize", "1")
  val sc = new SparkContext(conf)
}