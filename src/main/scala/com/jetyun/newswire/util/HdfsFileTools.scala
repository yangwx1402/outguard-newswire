package com.jetyun.newswire.util

import org.apache.hadoop.conf.Configuration
import java.io.PrintWriter
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import scala.io.Source

/**
 * @author 杨勇
 */
object HdfsFileTools {
  private val conf = new Configuration
  conf.set("", "")
  
  private val fs = FileSystem.get(conf)
  
  @throws(classOf[Exception])
  def writeFile(filePath:String,data:String){
    val printWriter = new PrintWriter(fs.create(new Path(filePath)))
    printWriter.write(data)
    printWriter.flush()
    printWriter.close()
  }
  @throws(classOf[Exception])
  def readLines(filePath:String):Array[String]={
    Source.fromInputStream(fs.open(new Path(filePath)), "utf-8").getLines().toArray
  }
}