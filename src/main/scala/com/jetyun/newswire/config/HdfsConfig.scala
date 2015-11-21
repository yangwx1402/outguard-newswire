package com.jetyun.newswire.config

import java.util.ResourceBundle
import java.util.Locale

/**
 * @author 杨勇
 */
object HdfsConfig {

  private val config = ResourceBundle.getBundle("hdfs", Locale.getDefault)

  def getConfigString(key: String): String = config.getString(key)
}