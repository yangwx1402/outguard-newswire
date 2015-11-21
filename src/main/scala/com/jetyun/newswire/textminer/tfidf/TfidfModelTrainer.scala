package com.jetyun.newswire.textminer.tfidf

import org.apache.spark.rdd.RDD
import com.jetyun.newswire.textminer.analyzer.SparkMmseg

/**
 * @author 杨勇
 * 训练tfidf模型
 */
object TfidfModelTrainer {
  def train(data: RDD[String]): Array[(String, Double)] = {
    //训练文档总数
    val docNum = data.count()
    //单词出现文档个数
    val wordDocNum = data.map { line =>
      val words = SparkMmseg.seg(line, ",").split(",").map { x => (x, 1) }.toMap
      words.toArray
    }.flatMap(x => x).reduceByKey(_ + _)
    //计算idf
    val idf = wordDocNum.map(word =>
      (word._1, Math.log(docNum * 1.0 / word._2))).collect()
    idf
  }
}