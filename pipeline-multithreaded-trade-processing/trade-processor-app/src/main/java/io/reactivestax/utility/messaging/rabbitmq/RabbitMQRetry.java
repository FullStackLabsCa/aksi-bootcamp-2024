package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.RabbitMQException;
import io.reactivestax.utility.messaging.MessageRetry;

import java.util.HashMap;
import java.util.Map;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class RabbitMQRetry implements MessageRetry<Trade> {
    private static RabbitMQRetry instance;

    private RabbitMQRetry() {
    }

    public static synchronized RabbitMQRetry getInstance(){
        if(instance == null) instance = new RabbitMQRetry();
        return instance;
    }


    @Override
    public void retryMessage(Trade trade) {
        try {
            Channel rabbitMQChannel = RabbitMQUtils.getRabbitMQChannel();
            rabbitMQChannel.exchangeDeclare(getFileProperty("rabbitMQ.dlx.exchange.name"), "direct");

            Map<String, Object> dlqArguments = new HashMap<>();
            dlqArguments.put("x-message-ttl", 5000); // Retry delay in milliseconds (5 seconds)
            dlqArguments.put("x-dead-letter-exchange", getFileProperty("rabbitMQ.main.exchange.name")); // Requeue to main exchange
            dlqArguments.put("x-dead-letter-routing-key", getFileProperty("rabbitMQ.main.routingKey")); // Requeue to the main queue

            rabbitMQChannel.queueDeclare(getFileProperty("rabbitMQ.dl.queue.name"), true, false, false, dlqArguments);
            rabbitMQChannel.queueBind(getFileProperty("rabbitMQ.dl.queue.name"), getFileProperty("rabbitMQ.dlx.exchange.name"), getFileProperty("rabbitMQ.dlx.routingKey"));


        } catch (Exception e) {
            System.out.println("Some issues in RabbitMQ Consumer...readFromRabbitMQ");
            e.printStackTrace();
            throw new RabbitMQException(e);
        }
    }
}
