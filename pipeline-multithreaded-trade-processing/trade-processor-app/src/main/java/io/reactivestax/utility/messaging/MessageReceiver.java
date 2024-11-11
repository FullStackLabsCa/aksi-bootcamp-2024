package io.reactivestax.utility.messaging;

import java.util.Optional;

public interface MessageReceiver<T> {
    Optional<T> receiveMessage(MessageProvider messageProvider);
}
