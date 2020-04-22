package com.cpic.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 黑名单练习
 */
object BlackListApp extends App {


  val sparkConf = new SparkConf().setMaster("local[2]").setAppName("BlackListApp")

  val ssc = new StreamingContext(sparkConf,Seconds(10))

  ssc.checkpoint("E:\\dataFile\\streaming\\checkpoint")

  val blacklist = List("zc","ls")

  val blackRDD = ssc.sparkContext.parallelize(blacklist).map(x => (x,true))

  val lines = ssc.socketTextStream("10.203.32.99",5679)


  /**
   * transform 可以与rdd交互
   */
  val result = lines.map(x => x.split(",")).map(x => (x(0),x)).transform(rdd => {
      rdd.leftOuterJoin(blackRDD).filter(x => x._2._2.getOrElse(false) != true).map(x => x._2._1)
    }).map(x => (x(0),x(1).toInt))

  /**
   * 这个计算的要求是dstream中的数据是(key,2)  前面是key，后面是数字的 tuple
   */
  val result2 = result.updateStateByKey(updateFunction)


  result2.print()


  /**
   * foreachRDD 重要的API
   * 将结果存入mysql中
   */
//  result.foreachRDD(rdd =>{
//    rdd.foreachPartition(patitionRecord => {
//      val connection = getConnetion()
//      patitionRecord.foreach(x =>{
//        val sql = "insert into word_count(word,wordcount) values ('"+x(0)+"',"+x(1)+") "
//
//        val stm = connection.createStatement()
//        stm.executeUpdate(sql)
//      })
//
//      connection.close()
//    })
//  })

  ssc.start()

  ssc.awaitTermination()


  def getConnetion() ={
    import java.sql.DriverManager
    Class.forName("com.mysql.cj.jdbc.Driver")
    val conn = DriverManager.getConnection("jdbc:mysql://10.203.32.99:3306/test?createDatabaseIfNotExist=true", "root", "root")
    conn
  }

  /**
   *
   * @param newValues  这个是之前的历史数据
   * @param runningCount  这个是这次的数据
   * @return
   */
  def updateFunction(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
    var newValue = 0
    newValue = runningCount.getOrElse(0)

    for(elem <- newValues){
      newValue +=elem
    }

    Some(newValue)
  }

}
