package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.messaging.MessageSender;

public class InMemorySender implements MessageSender<TradeIdAndAccNum> {
    private static InMemorySender instance;

    private InMemorySender() {
    }

    public static synchronized InMemorySender  getInstance(){
        if(instance == null) instance = new InMemorySender();
        return instance;
    }
    @Override
    public void sendMessage(TradeIdAndAccNum message) {
        //This is for later, not processing in memory stuff right now.
    }
}
