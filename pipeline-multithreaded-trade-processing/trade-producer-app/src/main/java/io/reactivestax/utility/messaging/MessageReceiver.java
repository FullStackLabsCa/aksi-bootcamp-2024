package io.reactivestax.utility.messaging;

public interface MessageReceiver<T> {
    T receiveMessage();
}
