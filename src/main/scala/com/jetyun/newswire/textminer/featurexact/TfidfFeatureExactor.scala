package com.jetyun.newswire.textminer.featurexact

import com.jetyun.newswire.textminer.tfidf.TfidfModel
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
import com.jetyun.newswire.textminer.analyzer.SparkMmseg
import org.apache.spark.mllib.linalg.Vectors
import scala.collection.mutable.HashMap
import com.jetyun.newswire.textminer.tfidf.WordTf

/**
 * @author 杨勇
 * 采用tfidf的方式抽取特征向量
 */
class TfidfFeatureExactor(model: TfidfModel) {
  def exact(articles: RDD[Article]): RDD[LabeledPoint] = {
    //这里需要考虑标题,关键字,内容的权重,参考下孙健的实现
    val features = articles.map { article =>
      val words = SparkMmseg.seg(article.content, ",").split(",")
      val map = new HashMap[String, Double]
      words.foreach(w => {
        if (map.contains(w)) {
          map.put(w, map.get(w).get + 1)
        } else {
          map.put(w, 1)
        }
      })
      val map2 = map.map(w => (w._1.hashCode(), model.predict(WordTf(w._1, w._2))))
      // map.foreach(x=>println("article_id="+article.id+":"+x) )
      val vector = Vectors.sparse(map.size, map2.keySet.toArray, map2.values.toArray)
      LabeledPoint(article.id, vector)
    }
    features
  }
}