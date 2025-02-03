package io.reactivestax.utility.messaging;

public interface MessageSender <T> {
    void sendMessage(T message);
}
