package com.cpic.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * nc -lk 5678
 */
object NetWordCount extends App {

  val sparkConf = new SparkConf().setMaster("local[2]").setAppName("NetWordCount")

  val ssc = new StreamingContext(sparkConf,Seconds(5))

  val lines = ssc.socketTextStream("10.203.32.99",5678)

  val words = lines.flatMap(_.split(" "))

  val wordCounts = words.map(x =>(x,1)).reduceByKey(_+_)

  wordCounts.print()

  ssc.start()
  ssc.awaitTermination()

}

