package io.reactivestax.utility.messaging.kafka;

import io.reactivestax.model.Trade;
import io.reactivestax.service.TradeProcessorService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;

public class KafkaConsumer {
    private final Consumer<String, String> consumer = KafkaUtils.getConsumer();

    public void startConsuming(){
        KafkaUtils.subscribeConsumerToTopic(consumer,"trades-topic");

        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
//                    System.out.printf("Consumed from %s | partition=%d | offset=%d | key=%s | value=%s%n",
//                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                    Trade trade = TradeProcessorService.getInstance().validatePayloadAndCreateTrade(record.value());
                    TradeProcessorService.getInstance().processTrade(trade);
                }
//                consumer.commitSync();
            }
        }).start();
    }

    public void closeConsuming(){
        consumer.close();
    }
}
