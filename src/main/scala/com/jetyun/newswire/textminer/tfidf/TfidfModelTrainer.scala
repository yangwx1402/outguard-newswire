package com.jetyun.newswire.textminer.tfidf

import org.apache.spark.rdd.RDD
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions

import com.jetyun.newswire.textminer.analyzer.SparkMmseg

/**
 * @author 杨勇
 * 训练tfidf模型
 */
object TfidfModelTrainer {
  def train(data: RDD[String]): TfidfModel = {
    val sparkMmseg = new SparkMmseg
    //训练文档总数
    val docNum = data.count()
    //单词出现文档个数
    val wordDocNum = data.map { line =>
      val words = sparkMmseg.seg(line, ",").split(",").filter { x => x.matches("[\\u4e00-\\u9fa5]*") }.map { x => (x, 1) }.toMap
      words.toArray
    }.flatMap(x => x).reduceByKey(_ + _)
    //计算idf
    val idf = wordDocNum.map(word =>
      (word._1, Math.log(docNum * 1.0 / word._2))).collect()
    val model = new TfidfModel
    model.addIdfs(idf)
    model
  }
}