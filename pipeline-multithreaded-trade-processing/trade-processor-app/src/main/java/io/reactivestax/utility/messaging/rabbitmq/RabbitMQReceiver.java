package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.utility.exceptions.RabbitMQException;
import io.reactivestax.utility.messaging.MessageReceiver;

import java.util.HashMap;
import java.util.Map;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class RabbitMQReceiver implements MessageReceiver<String> {
    private static RabbitMQReceiver instance;

    private RabbitMQReceiver() {
    }

    public static synchronized RabbitMQReceiver getInstance(){
        if(instance == null) instance = new RabbitMQReceiver();
        return instance;
    }

    @Override
    public String receiveMessage() {
        try{
            Channel rabbitMQChannel = RabbitMQUtils.getRabbitMQChannel();
            rabbitMQChannel.exchangeDeclare(getFileProperty("rabbitMQ.main.exchange.name"), "direct");

            Map<String, Object> mainQueueArguments = new HashMap<>();
            mainQueueArguments.put("x-queue-type", "quorum"); // Declare quorum queue
            mainQueueArguments.put("x-dead-letter-exchange", getFileProperty("rabbitMQ.dlx.exchange.name")); // If a message is rejected, send to DLX
            mainQueueArguments.put("x-dead-letter-routing-key", getFileProperty("rabbitMQ.dlx.routingKey"));

            rabbitMQChannel.queueDeclare(getFileProperty("rabbitMQ.queueName"), true, false, false, mainQueueArguments);
            rabbitMQChannel.queueBind(getFileProperty("rabbitMQ.queueName"), getFileProperty("rabbitMQ.main.exchange.name"), getFileProperty("rabbitMQ.main.routingKey"));

            System.out.println(" [*] Waiting for messages in '" + getFileProperty("rabbitMQ.queueName") + "'.");

            GetResponse response = rabbitMQChannel.basicGet(getFileProperty("rabbitMQ.queueName"), false);  // Fetch one message without auto-acknowledgment
            if (response != null) {
                String message = new String(response.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                // Manually acknowledge the message after processing
                rabbitMQChannel.basicAck(response.getEnvelope().getDeliveryTag(), false);

                RabbitMQUtils.closeRabbitMQChannel();
                // Return the received message
                return message;
            } else {
                System.out.println(" [x] No messages available in the queue.");
                return null;  // No message was available at the moment
            }
        }
        catch (Exception e) {
            System.out.println("Some issues in RabbitMQ Consumer...readFromRabbitMQ");
            e.printStackTrace();
            throw new RabbitMQException(e);
        }
    }
}
