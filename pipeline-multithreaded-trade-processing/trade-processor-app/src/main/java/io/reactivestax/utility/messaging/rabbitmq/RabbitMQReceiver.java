package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.utility.exceptions.RabbitMQException;
import io.reactivestax.utility.messaging.MessageProvider;
import io.reactivestax.utility.messaging.MessageReceiver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;

public class RabbitMQReceiver implements MessageReceiver<String> {
    private static RabbitMQReceiver instance;
    private volatile boolean isInitialized = false;
    private static final ReentrantLock lock = new ReentrantLock();

    private RabbitMQReceiver() {
    }

    public static synchronized RabbitMQReceiver getInstance(){
        if(instance == null) instance = new RabbitMQReceiver();
        return instance;
    }

    private static void initializeRabbitMQMainExchange(RabbitMQMessageProvider messageProvider){
        try {
            Channel rabbitMQChannel = RabbitMQUtils.getInstance().getRabbitMQChannel();
            rabbitMQChannel.exchangeDeclare(messageProvider.getMainExchangeName(), "direct");

            Map<String, Object> mainQueueArguments = new HashMap<>();
            mainQueueArguments.put("x-queue-type", "quorum"); // Declare quorum queue
            mainQueueArguments.put("x-dead-letter-exchange", messageProvider.getRetryExchangeName()); // If a message is rejected, send to DLX
            mainQueueArguments.put("x-dead-letter-routing-key", messageProvider.getRetryQueueRoutingKey());
//            mainQueueArguments.put("x-dead-letter-routing-key", getFileProperty("rabbitMQ.main.queue1.routingKey") + "_retry");

            rabbitMQChannel.queueDeclare(messageProvider.getMainQueueName(), true, false, false, mainQueueArguments);
            rabbitMQChannel.queueBind(messageProvider.getMainQueueName(), messageProvider.getMainExchangeName(), messageProvider.getMainQueueRoutingKey());

        } catch (Exception e) {
            System.out.println("Error Initializing RabbitMQ Receiver Main....");
        }
    }

    private void ensureRabbitMQExchangeInitialized(RabbitMQMessageProvider messageProvider){
        if (!isInitialized) {
            lock.lock();
            try {
                if (!isInitialized) {
                    initializeRabbitMQMainExchange(messageProvider);
                    isInitialized = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String receiveMessage(MessageProvider messageProvider) {
        try{
            ensureRabbitMQExchangeInitialized((RabbitMQMessageProvider) messageProvider);
            Channel rabbitMQChannel = RabbitMQUtils.getInstance().getRabbitMQChannel();

            System.out.println(" [*] Waiting for messages in '" + getFileProperty("rabbitMQ.main.queue1.name") + "'.");

            GetResponse response = rabbitMQChannel.basicGet(getFileProperty("rabbitMQ.main.queue1.name"), false);  // Fetch one message without auto-acknowledgment
            if (response != null) {
                RabbitMQUtils.getInstance().setThreadResponse(response);

                String message = new String(response.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");

                // Manually acknowledge the message after processing
                rabbitMQChannel.basicAck(response.getEnvelope().getDeliveryTag(), false);

                // Return the received message
                return message;
            } else {
                System.out.println(" [x] No messages available in the queue.");
                return receiveMessage(messageProvider);  // No message was available at the moment
            }
        }
        catch (Exception e) {
            System.out.println("Some issues in RabbitMQ Consumer...readFromRabbitMQ");
            e.printStackTrace();
            throw new RabbitMQException(e);
        }
    }
}
