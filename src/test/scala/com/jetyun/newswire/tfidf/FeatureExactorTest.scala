package com.jetyun.newswire.tfidf

import com.jetyun.newswire.textminer.tfidf.TfidfModel
import com.jetyun.newswire.TestBase
import org.apache.commons.lang.StringUtils
import com.jetyun.newswire.textminer.featurexact.TfidfFeatureExactor
import com.jetyun.newswire.textminer.featurexact.Article

/**
 * @author Administrator
 */
object FeatureExactorTest extends TestBase {
  def main(args: Array[String]): Unit = {
    val model = new TfidfModel
    val modelPath = "E:\\data\\spark\\tfidf\\tfidf.model"
    model.loadModel(modelPath, true)
    val dataPath = "E:\\data\\spark\\tfidf\\news_tensite.txt"
    val trainData = sc.textFile(dataPath, 5).filter { line => !StringUtils.isBlank(line) }
    val data = trainData.randomSplit(Array[Double](0.0001,0.0999), 11L)(0)
    val exactor = new TfidfFeatureExactor(model)
    var index = 0
    val srcData = data.map { x =>
      index = index + 1
      Article(index, "", "", x)
    }
    val features = exactor.exact(srcData).collect()
    for (f <- features) {
      println(exactor.printFeature(f))
    }
  }
}