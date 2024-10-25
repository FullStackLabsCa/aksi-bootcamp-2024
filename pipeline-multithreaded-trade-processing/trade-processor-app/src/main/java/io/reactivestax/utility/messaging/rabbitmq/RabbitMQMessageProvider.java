package io.reactivestax.utility.messaging.rabbitmq;

import io.reactivestax.utility.messaging.MessageProvider;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RabbitMQMessageProvider implements MessageProvider {
    private String mainExchangeName;
    private String mainQueueName;
    private String mainQueueRoutingKey;

    private String retryExchangeName;
    private String retryQueueName;
    private String retryQueueRoutingKey;
}
