package io.reactivestax.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.interfaces.TradeIdAndAccNum;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.RabbitMQException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;
import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getRabbitMQChannel;

public class TradesStream implements Runnable {
    private static final ConcurrentHashMap<String, Integer> accToQueueMap = new ConcurrentHashMap<>();
    private static final List<LinkedBlockingDeque<String>> listOfQueues = new ArrayList<>();
    private static final ConcurrentHashMap<String, Integer> retryCountMapping = new ConcurrentHashMap<>();
    private static final LinkedBlockingDeque<String> dlq = new LinkedBlockingDeque<>();

    @Override
    public void run() {
        bringUpQueues();
    }


    public static void bringUpQueues() {
        int numberOfQueues = Integer.parseInt(getFileProperty("trade.processor.queue.count"));

        for (int i = 0; i < numberOfQueues; i++) {
            listOfQueues.add(new LinkedBlockingDeque<>());
        }
    }

    public static LinkedBlockingDeque<String> getQueue(int index) {
        return listOfQueues.get(index);
    }

    public static int getQueueMapping(TradeIdAndAccNum tradeIdentifiers) {
        String criteria = getFileProperty("trade.distribution.criteria");

        String criteriaField;
        if (criteria.equals("tradeID")) {
            criteriaField = tradeIdentifiers.tradeID();
        } else {
            criteriaField = tradeIdentifiers.accountNumber();
        }

        if (accToQueueMap.containsKey(criteriaField)) {
            return accToQueueMap.get(criteriaField);
        } else {
            int randomQueueNum = (int) (Math.random() * Integer.parseInt(getFileProperty("trade.processor.queue.count")));
            accToQueueMap.put(criteriaField, randomQueueNum);
            return randomQueueNum;
        }
    }

    public static void insertIntoQueue(TradeIdAndAccNum tradeIdentifiers) {
        int queueIndex = getQueueMapping(tradeIdentifiers);

        try {
            listOfQueues.get(queueIndex).put(tradeIdentifiers.tradeID());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static void insertIntoRabbitMQQueue(String exchangeName, TradeIdAndAccNum tradeIdentifiers, Channel channel) {
        try {
            channel.exchangeDeclare(exchangeName, "direct");

            String routingKey = getRoutingKey(tradeIdentifiers);
            String message = tradeIdentifiers.tradeID();
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "' with routing key '" + routingKey + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRoutingKey(TradeIdAndAccNum tradeIdentifiers) {
        return "cc_partition_" + getQueueMapping(tradeIdentifiers);
    }

    public static String readFromRabbitMQ(String exchangeName, String queueName, String routingKey){
        try (Channel rabbitMQChannel = getRabbitMQChannel()) {

            rabbitMQChannel.exchangeDeclare(exchangeName, "direct");
            rabbitMQChannel.queueDeclare(queueName, true, false, false, null);
            rabbitMQChannel.queueBind(queueName, exchangeName, routingKey);

            System.out.println(" [*] Waiting for messages in '" + queueName + "'.");

            GetResponse response = rabbitMQChannel.basicGet(queueName, false);  // Fetch one message without auto-acknowledgment
            if (response != null) {
                String message = new String(response.getBody(), "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                // Manually acknowledge the message after processing
                rabbitMQChannel.basicAck(response.getEnvelope().getDeliveryTag(), false);

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

    public static void checkRetryCountAndManageDLQ(Trade trade, LinkedBlockingDeque<String> tradeIdQueue) throws InterruptedException {
        if (retryCountMapping.containsKey(trade.getTradeID())) {
            retryCountMapping.put(trade.getTradeID(), retryCountMapping.get(trade.getTradeID()) - 1);
        } else {
            retryCountMapping.put(trade.getTradeID(), Integer.parseInt(getFileProperty("retry.count")) - 1);
        }

        //DLQ
        if (retryCountMapping.get(trade.getTradeID()) == 0) {
            dlq.put(trade.getTradeID());
            System.out.println("Failed To Insert after 3 Retries: " + trade.getTradeID());
        } else {
            tradeIdQueue.putFirst(trade.getTradeID());
        }
    }
}
