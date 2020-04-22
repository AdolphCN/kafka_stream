package com.cpic.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object FlumePullStreamingApp{

  def main(args: Array[String]): Unit = {

    if(args.length < 2){
      System.out.println("Usage args <2")
      System.exit(1)
    }

    val sparkConf = new SparkConf().setAppName("FlumePullStreamingApp").setMaster("local[2]")

    val ssc = new StreamingContext(sparkConf,Seconds(10))

    //192.168.1.139   10.203.32.99  10.203.32.93
    val flumeStream = FlumeUtils.createPollingStream(ssc,args(0),args(1).toInt)

    flumeStream.map(x => new String(x.event.getBody.array()).trim)
      .flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).print()




    ssc.start()
    ssc.awaitTermination()
  }


}
