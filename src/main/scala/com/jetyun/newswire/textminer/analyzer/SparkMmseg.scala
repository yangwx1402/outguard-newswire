package com.jetyun.newswire.textminer.analyzer

import com.chenlb.mmseg4j.Dictionary
import com.chenlb.mmseg4j.ComplexSeg
import com.chenlb.mmseg4j.Seg
import java.io.IOException
import java.io.Reader
import com.chenlb.mmseg4j.Word
import com.chenlb.mmseg4j.MMSeg
import java.io.StringReader

/**
 * @author 杨勇
 * 分词器
 */
class SparkMmseg extends Serializable{
  private val seg: Seg = new ComplexSeg()

  /**
   * 可以通过这里改善分词,比如过滤掉一些停用词
   */
  private def segWords(input: Reader, wordSpilt: String): String = {
    val sb = new StringBuilder();
    val mmSeg = new MMSeg(input, seg);
    var word = mmSeg.next();
    var first = true;
    while (word != null) {
      if (!first) {
        sb.append(wordSpilt);
      }
      val w = word.getString();
      sb.append(w);
      first = false;
      word = mmSeg.next()
    }
    sb.toString();
  }

  private def segWords(txt: String, wordSpilt: String): String = {
    return segWords(new StringReader(txt), wordSpilt);
  }
  
  def seg(txt: String, wordSpilt: String): String = segWords(txt, wordSpilt)
  def seg(input: Reader, wordSpilt: String): String = segWords(input, wordSpilt)
}
