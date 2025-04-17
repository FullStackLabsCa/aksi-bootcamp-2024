package io.reactivestax;

import io.reactivestax.utility.messaging.kafka.KafkaConsumer;

public class TradeProcessingAppRunner {

    public static void main(String[] args) {
        new KafkaConsumer().startConsuming();
        new KafkaConsumer().startConsuming();
        new KafkaConsumer().startConsuming();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Shutdown hook triggered. Cleaning up...")));
    }
}
