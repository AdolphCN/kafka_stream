package com.cpic.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object KafkaStreamingApp {

  def main(args: Array[String]): Unit = {

    //    if (args.length < 2) {
    //      System.out.println("Usage args <2")
    //      System.exit(1)
    //    }

    val sparkConf = new SparkConf().setAppName("KafkaStreamingApp").setMaster("local[2]")

    val ssc = new StreamingContext(sparkConf, Seconds(10))

    //192.168.1.139   10.203.32.99  10.203.32.93
//    val kafkaParams = Map[String, String](
//      "bootstrap.servers" -> "192.168.1.139:9092"
//    )

    //    val kafkaStream = KafkaUtils.createDirectStream(ssc, kafkaParams, topics)
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.1.139:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("kafkatest", "topicB")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

//    stream.map(record => (record.key, record.value)).print()
    stream.map(record =>{
//      val index = record.value().lastIndexOf("-")
      (record.value(),1)
    }).print()

    ssc.start()
    ssc.awaitTermination()


//    spark-submit \
//      --master local[2] \
//      --name KafkaStreamingApp \
//      --class com.cpic.spark.KafkaStreamingApp \
//      --jars /root/hadoop/source/kafka_2.11/spark-streaming-kafka-0-10_2.11-2.4.0.jar,/root/hadoop/source/kafka_2.11/kafka-clients-2.4.0.jar \
//      /root/hadoop/source/kafka_2.11/kafka_stream-1.0.jar
  }


}
