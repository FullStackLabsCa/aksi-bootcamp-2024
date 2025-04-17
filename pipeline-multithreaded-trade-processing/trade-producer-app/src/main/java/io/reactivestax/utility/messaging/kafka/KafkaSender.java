package io.reactivestax.utility.messaging.kafka;

import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.messaging.KafkaMessageSender;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaSender implements KafkaMessageSender<TradeIdAndAccNum, String> {

    private static KafkaSender instance;

    private KafkaSender() {
    }

    public static synchronized KafkaSender getInstance(){
        if(instance == null) instance = new KafkaSender();
        return instance;
    }

    @Override
    public void sendMessage(TradeIdAndAccNum key, String value) {
        System.out.println("Sending Message to Topic");
        Producer<String, String> producer = KafkaUtils.getProducer();
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(ApplicationPropertyUtils.getFileProperty("kafka.topic.name"), key.accountNumber(), value);
        producer.send(producerRecord, (metadata, exception) -> {
            if (exception == null) {
                System.out.printf("Sent to %s | partition=%d | offset=%d%n",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                System.err.println("Error sending message: " + exception.getMessage());
            }
        });
    }
}
