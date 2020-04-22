package com.cpic.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object FileWordCount extends App{

  val sparkConf = new SparkConf().setMaster("local[2]").setAppName("FileWordCount")

  val ssc = new StreamingContext(sparkConf,Seconds(5))

  val lines = ssc.textFileStream("E:\\dataFile\\fileStreamingTest")

  val words = lines.flatMap(_.split(" "))

  val wordCount = words.map(x => (x,1)).reduceByKey(_+_)

  wordCount.print()

  ssc.start()
  ssc.awaitTermination()
}
