package io.reactivestax.utility.messaging.kafka;

import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.messaging.KafkaMessageSender;

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
        System.out.println("key = " + key);
        System.out.println("value = " + value);
        System.out.println();
    }
}
