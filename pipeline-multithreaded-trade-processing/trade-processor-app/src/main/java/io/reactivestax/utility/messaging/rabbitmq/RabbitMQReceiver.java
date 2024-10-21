package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.utility.exceptions.RabbitMQException;
import io.reactivestax.utility.messaging.MessageReceiver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class RabbitMQReceiver implements MessageReceiver<String> {
    private static RabbitMQReceiver instance;
    private static volatile boolean isInitialized = false;
    private static final ReentrantLock lock = new ReentrantLock();

    private RabbitMQReceiver() {
    }

    public static synchronized RabbitMQReceiver getInstance(){
        if(instance == null) instance = new RabbitMQReceiver();
        return instance;
    }

    private static void initializeRabbitMQMainExchange(){
        try {
            Channel rabbitMQChannel = RabbitMQUtils.getInstance().getRabbitMQChannel();
            rabbitMQChannel.exchangeDeclare(getFileProperty("rabbitMQ.main.exchange.name"), "direct");

            Map<String, Object> mainQueueArguments = new HashMap<>();
            mainQueueArguments.put("x-queue-type", "quorum"); // Declare quorum queue
            mainQueueArguments.put("x-dead-letter-exchange", getFileProperty("rabbitMQ.retry.exchange.name")); // If a message is rejected, send to DLX
            mainQueueArguments.put("x-dead-letter-routing-key", getFileProperty("rabbitMQ.main.routingKey") + "_retry");

            rabbitMQChannel.queueDeclare(getFileProperty("rabbitMQ.main.queue.name"), true, false, false, mainQueueArguments);
            rabbitMQChannel.queueBind(getFileProperty("rabbitMQ.main.queue.name"), getFileProperty("rabbitMQ.main.exchange.name"), getFileProperty("rabbitMQ.main.routingKey"));

        } catch (Exception e) {
            System.out.println("Error Initializing RabbitMQ Receiver Main....");
        }
    }

    private static void ensureRabbitMQExchangeInitialized(){
        if (!isInitialized) {
            lock.lock();
            try {
                if (!isInitialized) {
                    initializeRabbitMQMainExchange();
                    isInitialized = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String receiveMessage() {
        try{
            ensureRabbitMQExchangeInitialized();

            Channel rabbitMQChannel = RabbitMQUtils.getInstance().getRabbitMQChannel();

            System.out.println(" [*] Waiting for messages in '" + getFileProperty("rabbitMQ.main.queue.name") + "'.");

            GetResponse response = rabbitMQChannel.basicGet(getFileProperty("rabbitMQ.main.queue.name"), false);  // Fetch one message without auto-acknowledgment
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
                return receiveMessage();  // No message was available at the moment
            }
        }
        catch (Exception e) {
            System.out.println("Some issues in RabbitMQ Consumer...readFromRabbitMQ");
            e.printStackTrace();
            throw new RabbitMQException(e);
        }
    }
}
