package com.jetyun.newswire.textminer.featurexact

import scala.collection.mutable.HashMap

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

import com.jetyun.newswire.textminer.analyzer.SparkMmseg
import com.jetyun.newswire.textminer.tfidf.TfidfModel
import com.jetyun.newswire.textminer.tfidf.WordTf

/**
 * @author 杨勇
 * 采用tfidf的方式抽取特征向量
 */
class TfidfFeatureExactor(model: TfidfModel) extends Serializable {
  def exact(articles: RDD[Article]): RDD[LabeledPoint] = {
    val sparkMmseg = new SparkMmseg
    //这里需要考虑标题,关键字,内容的权重,参考下孙健的实现
    val features = articles.map { article =>
      val words = sparkMmseg.seg(article.content, ",").split(",").filter { x => x.matches("[\\u4e00-\\u9fa5]*")&&x.length()>1 }
      val map = new HashMap[String, Double]
      words.foreach(w => {
        if (map.contains(w)) {
          map.put(w, map.get(w).get + 1)
        } else {
          map.put(w, 1)
        }
      })
      val temp = map.toArray
      val map2 = temp.map(w => (w._1.hashCode(), model.predict(WordTf(w._1, w._2)))).sortBy(-_._2)
      val indexs = map2.map(_._1)
      val values = map2.map(_._2)
      // map.foreach(x=>println("article_id="+article.id+":"+x) )
      val vector = Vectors.sparse(map.size, indexs, values)
      LabeledPoint(article.id, vector)
    }
    features
  }

  def printFeature(point: LabeledPoint): String = {
    val label = point.label
    val size = point.features.size
    val indexs = point.features.toSparse.indices
    val values = point.features.toSparse.values
    val buffer = new StringBuilder
    buffer.append("label=" + label + " feature=[")
    for (i <- 0 until size) {
      buffer.append("" + model.index(indexs(i)) + ":" + values(i) + ",")
    }
    buffer.append("]")
    buffer.toString()
  }
}