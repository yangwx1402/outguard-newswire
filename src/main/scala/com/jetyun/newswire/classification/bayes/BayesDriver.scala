package com.jetyun.newswire.classification.bayes

import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.SparkContext

/**
 * @author 杨勇
 * 贝叶斯分类器实现
 */
object BayesDriver {

  def trainModel(trainData: RDD[LabeledPoint], trainType: String = "multinomial", lamda: Double = 1.0): NaiveBayesModel = NaiveBayes.train(trainData, lamda, trainType)

  def splitData(data: RDD[LabeledPoint], weights: Array[Double], seed: Long = 11L) = data.randomSplit(weights, seed)

  def saveModel(sc: SparkContext, model: NaiveBayesModel, path: String) {
    model.save(sc, path)
  }
  
  def loadModel(sc: SparkContext, path: String): NaiveBayesModel = NaiveBayesModel.load(sc, path)

}