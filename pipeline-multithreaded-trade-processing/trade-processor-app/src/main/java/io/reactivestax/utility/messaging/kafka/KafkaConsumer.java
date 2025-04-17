package io.reactivestax.utility.messaging.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;

public class KafkaConsumer {
    private static Consumer<String, String> consumer = KafkaUtils.getConsumer();

    public static void startConsuming(){
        KafkaUtils.subscribeConsumerToTopic(consumer,"trades-topic");

        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
//                    receivedMessage = record.value();
                    System.out.printf("Consumed from %s | partition=%d | offset=%d | key=%s | value=%s%n",
                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                }
//                consumer.commitSync();
            }
        }).start();
    }

    public static void closeConsuming(){
        consumer.close();
    }
}
