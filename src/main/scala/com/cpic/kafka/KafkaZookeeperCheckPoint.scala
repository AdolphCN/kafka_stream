package com.cpic.kafka

import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies, OffsetRange}

import scala.collection.mutable
import scala.collection.JavaConversions._

object KafkaZookeeperCheckPoint {

//https://blog.csdn.net/jklcl/article/details/85217660
  //https://www.cnblogs.com/kpsmile/p/10490283.html
  // ZK client
  val client = {
    val client = CuratorFrameworkFactory
      .builder
      .connectString("10.203.32.99:2181")
      .retryPolicy(new ExponentialBackoffRetry(1000, 3))
      .namespace("mykafka")
      .build()
    client.start()
    client
  }
  // offset 路径起始位置
  val Globe_kafkaOffsetPath = "/kafka/offsets"

  // 路径确认函数  确认ZK中路径存在，不存在则创建该路径
  def ensureZKPathExists(path: String)={

    val temppath = s"$path/0"
    if (client.checkExists().forPath(temppath) == null) {
//      client.create().creatingParentsIfNeeded().forPath()
      client.create().creatingParentsIfNeeded().forPath(temppath,Array("0".toByte))
    }

  }


  // 保存 新的 offset
  def storeOffsets(offsetRange: Array[OffsetRange], groupName:String) = {

    for (o <- offsetRange){
      val zkPath = s"${Globe_kafkaOffsetPath}/${groupName}/${o.topic}/${o.partition}"

      // 向对应分区第一次写入或者更新Offset 信息
      println("---Offset写入ZK------\nTopic：" + o.topic +", Partition:" + o.partition + ", Offset:" + o.untilOffset)
      client.setData().forPath(zkPath, o.untilOffset.toString.getBytes())
    }
  }

  def getFromOffset(topic: Array[String], groupName:String):(Map[TopicPartition, Long], Int) = {

    // Kafka 0.8和0.10的版本差别，0.10 为 TopicPartition   0.8 TopicAndPartition
    var fromOffset: Map[TopicPartition, Long] = Map()

    val topic1 = topic(0).toString

    // 读取ZK中保存的Offset，作为Dstrem的起始位置。如果没有则创建该路径，并从 0 开始Dstream
    val zkTopicPath = s"${Globe_kafkaOffsetPath}/${groupName}/${topic1}"

    // 检查路径是否存在
    ensureZKPathExists(zkTopicPath)

    // 获取topic的子节点，即 分区
    val childrens = client.getChildren().forPath(zkTopicPath)

    // 遍历分区
    val offSets: mutable.Buffer[(TopicPartition, Long)] = for {
      p <- childrens
    }
      yield {

        // 遍历读取子节点中的数据：即 offset
        val offsetData = client.getData().forPath(s"$zkTopicPath/$p")
        // 将offset转为Long
//        val offSet = java.lang.Long.valueOf(new String(offsetData)).toLong
        // 返回  (TopicPartition, Long)
        (new TopicPartition(topic1, Integer.parseInt(p)), 0l)
      }
    println(offSets.toMap)
    if(offSets.isEmpty){
      (offSets.toMap, 0)
    } else {
      (offSets.toMap, 1)
    }


  }

  //    if (client.checkExists().forPath(zkTopicPath) == null){
  //
  //      (null, 0)
  //    }
  //    else {
  //      val data = client.getData.forPath(zkTopicPath)
  //      println("----------offset info")
  //      println(data)
  //      println(data(0))
  //      println(data(1))
  //      val offSets = Map(new TopicPartition(topic1, 0) -> 7332.toLong)
  //      println(offSets)
  //      (offSets, 1)
  //    }
  //
  //  }

  def createMyZookeeperDirectKafkaStream(ssc:StreamingContext, kafkaParams:Map[String, Object], topic:Array[String],
                                         groupName:String ):InputDStream[ConsumerRecord[String, String]] = {

    // get offset  flag = 1  表示基于已有的offset计算  flag = 表示从头开始(最早或者最新，根据Kafka配置)
    val (fromOffsets, flag) = getFromOffset(topic, groupName)
    var kafkaStream:InputDStream[ConsumerRecord[String, String]] = null
    if (flag == 1){
      // 加上消息头
      //val messageHandler = (mmd:MessageAndMetadata[String, String]) => (mmd.topic, mmd.message())
      println(fromOffsets)
      kafkaStream = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe(topic, kafkaParams, fromOffsets))
      println(fromOffsets)
      println("中断后 Streaming 成功！")

    } else {
      kafkaStream = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe(topic, kafkaParams))

      println("首次 Streaming 成功！")

    }
    kafkaStream
  }

  def main(args: Array[String]): Unit = {

    val processInterval = 5
//    val brokers = "hadoop001:9092,hadoop001:9093,hadoop001:9094"
//    val brokers = "10.203.32.99:9092,10.203.32.99:9093,10.203.32.99:9094"
    val brokers = "10.203.32.99:9092"
    val topics = Array("sparktest")
    val conf = new SparkConf().setMaster("local[2]").setAppName("kafka checkpoint zookeeper")
    // kafka params
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "zk_group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val ssc = new StreamingContext(conf, Seconds(processInterval))

//    val zkClient = new ZKClient("")
//    zkClient.getServiceData()

    val messages = createMyZookeeperDirectKafkaStream(ssc, kafkaParams, topics, "zk_group")

    messages.foreachRDD((rdd) => {
      if (!rdd.isEmpty()){

        println("###################:"+rdd.count())
      }

      // 存储新的offset
      storeOffsets(rdd.asInstanceOf[HasOffsetRanges].offsetRanges, "zk_group")
    })

    ssc.start()
    ssc.awaitTermination()

  }

}
