package io.reactivestax.utility.messaging.inmemory;

import io.reactivestax.utility.messaging.MessageReceiver;

public class InMemoryReceiver implements MessageReceiver<String> {
    private static InMemoryReceiver instance;

    private InMemoryReceiver() {
    }

    public static synchronized InMemoryReceiver  getInstance(){
        if(instance == null) instance = new InMemoryReceiver();
        return instance;
    }

    @Override
    public String receiveMessage() {
        return "";
    }
}
