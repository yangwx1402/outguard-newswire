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
class SparkMmseg {
  private val dic: Dictionary = Dictionary.getInstance("data")
  private val seg: Seg = new ComplexSeg(dic)

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
}
object SparkMmseg {
  private val segTool: SparkMmseg = new SparkMmseg
  def seg(txt: String, wordSpilt: String): String = segTool.segWords(txt, wordSpilt)
  def seg(input: Reader, wordSpilt: String): String = segTool.segWords(input, wordSpilt)
}