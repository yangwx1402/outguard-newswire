package com.jetyun.newswire.textminer.tfidf

import scala.collection.mutable.HashMap
import com.jetyun.newswire.util.LocalFileTools
import com.jetyun.newswire.util.HdfsFileTools
import org.apache.commons.lang.StringUtils

/**
 * @author 杨勇
 * tfidf模型
 */
class TfidfModel() extends Serializable {
  private val idf = new HashMap[String, Double]

  private val index = new HashMap[Int, String]

  def addIdf(word: String, idfValue: Double) {
    idf.put(word, idfValue)
    index.put(word.hashCode(), word)
  }

  def addIdfs(idfs: Array[(String, Double)]) {
    for (idfValue <- idfs) {
      addIdf(idfValue._1, idfValue._2)
    }
  }

  def predict(word: WordTf): Double = if (idf.contains(word.text)) word.tf * idf.get(word.text).get else 1.0 / 50 * word.tf

  def index(key:Int):String = if(index.contains(key)) index.get(key).get else "none"
  
  def saveModel(modelPath: String,localFile: Boolean = false) {
    val buffer = new StringBuilder
    for (entry <- idf) {
      buffer.append(entry._1 + ":" + entry._2 + "\n")
    }
    if (localFile) {
      LocalFileTools.writeFile(modelPath, buffer.toString())
    } else {
      HdfsFileTools.writeFile(modelPath, buffer.toString())
    }
  }

  def loadModel(modelPath: String,localFile: Boolean = false) {
    var dataArray = Array[String]()
    if (localFile)
      dataArray = LocalFileTools.readLines(modelPath)
    else
      dataArray = HdfsFileTools.readLines(modelPath)
    val idfs = dataArray.filter { x => !StringUtils.isBlank(x) }.map { line =>
      val temp = line.split(":")
      (temp(0), temp(1).toDouble)
    }
    addIdfs(idfs)
  }
}