package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.RabbitMQException;
import io.reactivestax.utility.messaging.MessageRetry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class RabbitMQRetry implements MessageRetry<Trade> {
    private static RabbitMQRetry instance;
    private static volatile boolean isInitialized = false;
    private static final ReentrantLock lock = new ReentrantLock();

    private RabbitMQRetry() {
    }

    public static synchronized RabbitMQRetry getInstance(){
        if(instance == null) instance = new RabbitMQRetry();
        return instance;
    }

    private static void initializeRabbitMQ(){
        try {
            Channel rabbitMQChannel = RabbitMQUtils.getRabbitMQChannel();
            rabbitMQChannel.exchangeDeclare(getFileProperty("rabbitMQ.dlx.exchange.name"), "direct");

            Map<String, Object> dlqArguments = new HashMap<>();
            dlqArguments.put("x-message-ttl", 5000); // Retry delay in milliseconds (5 seconds)
            dlqArguments.put("x-dead-letter-exchange", getFileProperty("rabbitMQ.main.exchange.name")); // Requeue to main exchange
            dlqArguments.put("x-dead-letter-routing-key", getFileProperty("rabbitMQ.main.routingKey")); // Requeue to the main queue

            rabbitMQChannel.queueDeclare(getFileProperty("rabbitMQ.dl.queue.name"), true, false, false, dlqArguments);
            rabbitMQChannel.queueBind(getFileProperty("rabbitMQ.dl.queue.name"), getFileProperty("rabbitMQ.dlx.exchange.name"), getFileProperty("rabbitMQ.dlx.routingKey"));

            RabbitMQUtils.closeRabbitMQChannel();
        } catch (Exception e) {
            System.out.println("Error Initializing RabbitMQ Retry....");
        }
    }

    private static void ensureRabbitMQInitialized() {
        if (!isInitialized) {
            lock.lock();
            try {
                if (!isInitialized) {
                    initializeRabbitMQ();
                    isInitialized = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void retryMessage(Trade trade) {
        try {
            ensureRabbitMQInitialized();

        } catch (Exception e) {
            System.out.println("Some issues in RabbitMQ Consumer...readFromRabbitMQ");
            e.printStackTrace();
            throw new RabbitMQException(e);
        }
    }
}
