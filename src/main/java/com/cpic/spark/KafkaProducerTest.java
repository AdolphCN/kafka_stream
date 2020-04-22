package com.cpic.spark;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerTest extends Thread{

    private String topic;

    private KafkaProducer<String, String> kafkaProducer;

    public KafkaProducerTest(String topic){
        this.topic = topic;

        Properties props = new Properties();

        props.put("bootstrap.servers", KafkaProperties.BROKER_LIST);
//        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        kafkaProducer = new KafkaProducer<String, String>(props);

    }

    @Override
    public void run() {
        int messageNo = 1;

        while (true){
            String message = "message"+messageNo;

            kafkaProducer.send(new ProducerRecord(topic,message,message));
            kafkaProducer.flush();
            System.out.println(message);
            messageNo ++;
            try{
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
