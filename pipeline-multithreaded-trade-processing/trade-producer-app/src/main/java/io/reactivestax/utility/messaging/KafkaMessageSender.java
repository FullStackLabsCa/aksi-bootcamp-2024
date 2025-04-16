package io.reactivestax.utility.messaging;

public interface KafkaMessageSender<K, V> {
    void sendMessage(K key, V value);
}
