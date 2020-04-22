package com.cpic.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 使用Flume+Streaming
 */
object FlumeStreamingApp extends App{

//  if(args.length <2){
//    System.err.println("Usage -------")
//    System.exit(-1)
//  }


//  list :List[hostname,port] = List(args(0),args(1))

  val sparkConf = new SparkConf()//.setMaster("local[2]").setAppName("FlumeStreamingApp")

  val ssc = new StreamingContext(sparkConf,Seconds(10))

  //192.168.1.139   10.203.32.99  10.203.32.93
  val flumeStream = FlumeUtils.createStream(ssc,"10.203.32.99", 5656)

  flumeStream.print()

  ssc.start()
  ssc.awaitTermination()

}
