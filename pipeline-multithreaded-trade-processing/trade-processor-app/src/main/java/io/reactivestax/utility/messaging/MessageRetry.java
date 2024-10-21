package io.reactivestax.utility.messaging;

public interface MessageRetry<T> {
    void retryMessage(T message);
}
