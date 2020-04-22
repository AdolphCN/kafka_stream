package com.cpic.spark;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumerTest  extends Thread{
    private String topic;

    KafkaConsumer<String, String> consumer;

    public KafkaConsumerTest(String topic){
        this.topic = topic;

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", KafkaProperties.BROKER_LIST);
        props.setProperty("group.id", "message");
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(props);
    }

    @Override
    public void run() {

        while (true) {
            consumer.subscribe(Arrays.asList(topic));

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }
    }
}
