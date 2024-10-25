package io.reactivestax.utility.messaging;

public interface MessageReceiver<T, V> {
    T receiveMessage(V messageProvider);
}
