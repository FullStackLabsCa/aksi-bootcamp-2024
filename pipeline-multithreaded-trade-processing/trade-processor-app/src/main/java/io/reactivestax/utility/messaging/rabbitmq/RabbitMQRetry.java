package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
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

    private static void ensureRabbitMQExchangeInitialized() {
        if (!isInitialized) {
            lock.lock();
            try {
                if (!isInitialized) {
                    initializeRabbitMQDLXExchange();
                    isInitialized = true;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static void initializeRabbitMQDLXExchange(){
        try {
            Channel rabbitMQChannel = RabbitMQUtils.getInstance().getRabbitMQChannel();

            //Retry Exchange Initialization
            rabbitMQChannel.exchangeDeclare(getFileProperty("rabbitMQ.retry.exchange.name"), "direct");

            Map<String, Object> dlqArguments = new HashMap<>();
            dlqArguments.put("x-message-ttl", 5000); // Retry delay in milliseconds (5 seconds)
            dlqArguments.put("x-dead-letter-exchange", getFileProperty("rabbitMQ.main.exchange.name")); // Requeue to main exchange
            dlqArguments.put("x-dead-letter-routing-key", getFileProperty("rabbitMQ.main.routingKey")); // Requeue to the main queue

            rabbitMQChannel.queueDeclare(getFileProperty("rabbitMQ.main.queue.name") + "_retry", true, false, false, dlqArguments);
            rabbitMQChannel.queueBind(getFileProperty("rabbitMQ.main.queue.name") + "_retry", getFileProperty("rabbitMQ.retry.exchange.name"), getFileProperty("rabbitMQ.main.routingKey")+"_retry");

            //Dead Letter Exchange Initialization
            rabbitMQChannel.exchangeDeclare(getFileProperty("rabbitMQ.dlx.exchange.name"), "direct", true);
            rabbitMQChannel.queueDeclare(getFileProperty("rabbitMQ.dlx.queue.name"), true, false, false, null);
            rabbitMQChannel.queueBind(getFileProperty("rabbitMQ.dlx.queue.name"), getFileProperty("rabbitMQ.dlx.exchange.name"), getFileProperty("rabbitMQ.dlx.routingKey"));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Initializing RabbitMQ Retry....");
        }
    }

    @Override
    public void retryMessage(Trade trade) {
        try {
            ensureRabbitMQExchangeInitialized();
            Channel rabbitMQChannel = RabbitMQUtils.getInstance().getRabbitMQChannel();

            GetResponse response = RabbitMQUtils.getInstance().getThreadResponse();
            int retryCount = getMessageRetryCount(response);

            if(retryCount > 1) {
                System.out.println("ssss");
            }
            if (retryCount < Integer.parseInt(getFileProperty("retry.count"))) {
                retryCount++;

                Map<String, Object> updatedHeaders = new HashMap<>();
                updatedHeaders.put("x-retry-count", retryCount);

                AMQP.BasicProperties retryProperties = new AMQP.BasicProperties.Builder()
                        .headers(updatedHeaders)
                        .build();

                // Re-Publish Message to Retry Exchange
                rabbitMQChannel.basicPublish(getFileProperty("rabbitMQ.retry.exchange.name"), getFileProperty("rabbitMQ.main.routingKey")+"_retry", retryProperties, trade.getTradeID().getBytes());
                System.out.println("Message retried. Retry count: " + retryCount);

            } else {
                // Move Message to Dead Letter Queue
                rabbitMQChannel.basicPublish(getFileProperty("rabbitMQ.dlx.exchange.name"), getFileProperty("rabbitMQ.dlx.routingKey"), null, trade.getTradeID().getBytes());
                System.out.println("Max retries reached. Message sent to DLQ.");
            }

        } catch (Exception e) {
            System.out.println("Some issues in RabbitMQ Consumer...readFromRabbitMQ");
            e.printStackTrace();
            throw new RabbitMQException(e);
        }
    }

    private int getMessageRetryCount(GetResponse response){
        Map<String, Object> headers = response.getProps().getHeaders();
        return (headers != null && headers.containsKey("x-retry-count"))
                ? (int) headers.get("x-retry-count")
                : 0;
    }
}
