package com.cpic.kafka

import com.cpic.spark.KafkaProperties
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.JavaConversions._

object KafkaZookeeperCheckPoint2 {

  // ZK client
  val client = {
    val client = CuratorFrameworkFactory
      .builder
      .connectString(KafkaProperties.ZK)
      .retryPolicy(new ExponentialBackoffRetry(1000, 3))
      .namespace("adolph")
      .build()
    client.start()
    client
  }

  // offset 路径起始位置
  val Globe_kafkaOffsetPath = "/kafka/offsets"

  // 路径确认函数  确认ZK中路径存在，不存在则创建该路径
  def ensureZKPathExists(path: String) = {

    if (client.checkExists().forPath(path) == null) {
      client.create().creatingParentsIfNeeded().forPath(path)
    }

  }

  // 保存 新的 offset
  def storeOffsets(offsetRange: Array[OffsetRange], groupName: String) = {

    for (o <- offsetRange) {
      val zkPath = s"${Globe_kafkaOffsetPath}/${groupName}/${o.topic}/${o.partition}"

      // 向对应分区第一次写入或者更新Offset 信息
      println("---Offset写入ZK------\nTopic：" + o.topic + ", Partition:" + o.partition + ", Offset:" + o.untilOffset)
      if (client.checkExists().forPath(zkPath) == null) {
        client.create().creatingParentsIfNeeded().forPath(zkPath, o.untilOffset.toString.getBytes())
      } else {
        client.setData().forPath(zkPath, o.untilOffset.toString.getBytes())
      }
    }
  }

  def getFromOffset(topics: Array[String], groupName: String): (Map[TopicPartition, Long], Int) = {

    var flag = 1

    val bufferMap = Map[TopicPartition, Long]()
    for (topic <- topics ; if flag == 1) {
      println(topic)
      val zkTopicPath = s"${Globe_kafkaOffsetPath}/${groupName}/${topic}"
      if (client.checkExists().forPath(zkTopicPath) == null) {
        flag = 0
      } else {
        val mmap = client.getChildren.forPath(zkTopicPath).map(p => {
          val offsetStr = client.getData.forPath(s"$zkTopicPath/$p")

          (new TopicPartition(topic, p.toInt), new String(offsetStr).toLong)
        })

        bufferMap ++ mmap
      }
    }

    (bufferMap, flag)

  }


  def createMyZookeeperDirectKafkaStream(ssc: StreamingContext, kafkaParams: Map[String, Object], topic: Array[String],
                                         groupName: String): InputDStream[ConsumerRecord[String, String]] = {


    //TODO ~~~~~~~~~~~~~~~~~~先从zk上获取，，如果没有，说明是首次消费
    // get offset  flag = 1  表示基于已有的offset计算  flag = 表示从头开始(最早或者最新，根据Kafka配置)
    val (fromOffsets, flag) = getFromOffset(topic, groupName)
    fromOffsets.foreach(print)
    println("~~~~~~~~~~~~~~~~~~~~~"+fromOffsets)
    var kafkaStream: InputDStream[ConsumerRecord[String, String]] = null
    if (flag == 1) {
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

    //    val brokers = "hadoop001:9092,hadoop001:9093,hadoop001:9094"
    //    val brokers = "10.203.32.99:9092,10.203.32.99:9093,10.203.32.99:9094"
    //    val brokers = "10.203.32.99:9092"
    val topics = Array("sparktest", "kafkatest")
    val conf = new SparkConf().setMaster("local[2]").setAppName("kafka checkpoint zookeeper")
    // kafka params
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> KafkaProperties.BROKER_LIST,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "zk_group",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val ssc = new StreamingContext(conf, Seconds(10))


    val messages = createMyZookeeperDirectKafkaStream(ssc, kafkaParams, topics, "zk_group")

    messages.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        rdd.foreach(x => println(x.key(), x.value()))
        println("###################:" + rdd.count())

        // 存储新的offset
        storeOffsets(rdd.asInstanceOf[HasOffsetRanges].offsetRanges, "zk_group")
      }


    })

    ssc.start()
    ssc.awaitTermination()

  }


}


