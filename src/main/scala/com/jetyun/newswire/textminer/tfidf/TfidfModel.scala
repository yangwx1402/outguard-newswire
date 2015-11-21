package com.jetyun.newswire.textminer.tfidf

import scala.collection.mutable.HashMap

/**
 * @author 杨勇
 * tfidf模型
 */
class TfidfModel extends Serializable{
  val idf = new HashMap[String, Double]
  def predict(word: WordTf): Double = if (idf.contains(word.text)) word.tf * idf.get(word.text).get else 1.0 / 50 * word.tf

  def saveModel(modelPath:String){
    
  }
  
  def loadModel(modelPath:String){
    
  }
}