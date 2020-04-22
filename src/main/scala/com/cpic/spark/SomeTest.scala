package com.cpic.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SomeTest extends App  {


  val spark = new SparkConf().setAppName("SomeTest").setMaster("local[2]")

  val ssc = new StreamingContext(spark,Seconds(10))

  val black = List("zc","ls“,”ww")

  val blackRDD = ssc.sparkContext.parallelize(black).map(x => (x,true))






  val word = List("zc,100","ls,80","ww,120")

  val wordsRDD = ssc.sparkContext.parallelize(word).map(x => x.split(",")).map(x => (x(0),x))
      .leftOuterJoin(blackRDD).filter(x => x._2._2 ==true).flatMap(x => x._2._1(0))

  wordsRDD.collect().foreach(println)


  var seq = Seq[String]()
  seq = seq :+ "hello"

  println(seq)

  var set = Set[String]()
  set += "hello"
println(set)

//  wo
//
//  words.collect.foreach(x => println("word"+x(1)))


println("end")

}
