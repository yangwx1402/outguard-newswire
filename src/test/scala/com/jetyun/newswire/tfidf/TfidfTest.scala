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
    try{
    val dataPath = "E:\\data\\spark\\tfidf\\test.data"
    val trainData = sc.textFile(dataPath, 1).filter { line => !StringUtils.isBlank(line) }
    val tfidfModel = TfidfModelTrainer.train(trainData)
    val exactor = new TfidfFeatureExactor(tfidfModel)
    var index = 0
    val srcData = trainData.map { x => 
      index = index+1
      Article(index, "", "", x) }
    val features = exactor.exact(srcData)
    features.foreach { println _ }
    }catch{
      case e:Exception => e.printStackTrace()
      FileUtils.writeStringToFile(new File("E:\\error.log"), e.getCause.toString(), false)
    }
  }
}