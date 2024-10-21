package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.messaging.MessageRetry;

public class InMemoryRetry implements MessageRetry<Trade> {
    private static InMemoryRetry instance;

    private InMemoryRetry() {
    }

    public static synchronized InMemoryRetry getInstance(){
        if(instance == null) instance = new InMemoryRetry();
        return instance;
    }


    @Override
    public void retryMessage(Trade trade) {

    }
}
