package com.jetyun.newswire.tfidf

import org.apache.spark.SparkContext
import com.jetyun.newswire.TestBase
import org.apache.commons.lang.StringUtils
import com.jetyun.newswire.textminer.tfidf.TfidfModelTrainer
import com.jetyun.newswire.textminer.featurexact.TfidfFeatureExactor
import com.jetyun.newswire.textminer.featurexact.Article
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * @author Administrator
 */
object TfidfTest extends TestBase {

  def main(args: Array[String]): Unit = {
    try {
      val dataPath = "E:\\data\\spark\\tfidf\\news_tensite.txt"
      val modelPath = "E:\\data\\spark\\tfidf\\tfidf.model"
      val trainData = sc.textFile(dataPath, 5).filter { line => !StringUtils.isBlank(line) }
      //trainData.foreach { println _ }
      val tfidfModel = TfidfModelTrainer.train(trainData)
//      val exactor = new TfidfFeatureExactor(tfidfModel)
//      var index = 0
//      val srcData = trainData.map { x =>
//        index = index + 1
//        Article(index, "", "", x)
//      }
//      val features = exactor.exact(srcData).collect()
//      for(f<-features){
//        println(exactor.printFeature(f))
//      }
    tfidfModel.saveModel(modelPath,true)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        FileUtils.writeStringToFile(new File("E:\\error.log"), e.getCause.toString(), false)
    }
  }
}