package com.jetyun.newswire.util

import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

import scala.io.Source

/**
 * @author 杨勇
 */
object LocalFileTools {
   @throws(classOf[Exception])
  def writeFile(filePath:String,data:String){
    val printWriter = new PrintWriter(new FileOutputStream(filePath))
    printWriter.write(data)
    printWriter.flush()
    printWriter.close()
  }
  @throws(classOf[Exception])
  def readLines(filePath:String):Array[String]={
    Source.fromFile(new File(filePath), "utf-8").getLines().toArray
  }
}