package io.reactivestax.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.reactivestax.interfaces.TradeIdAndAccNum;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.MultiThreadTradeProcessorUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;
import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.rabbitMQFactory;

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
        int numberOfQueues = Integer.parseInt(getFileProperty("numberOfQueues"));

        for (int i = 0; i < numberOfQueues; i++) {
            listOfQueues.add(new LinkedBlockingDeque<>());
        }
    }

    public static LinkedBlockingDeque<String> getQueue(int index) {
        return listOfQueues.get(index);
    }

    public static int getQueueMapping(TradeIdAndAccNum tradeIdentifiers) {
        String criteria = getFileProperty("tradeDistributionCriteria");

        String criteriaField;
        if (criteria.equals("tradeID")) {
            criteriaField = tradeIdentifiers.tradeID();
        } else {
            criteriaField = tradeIdentifiers.accountNumber();
        }

        if (accToQueueMap.containsKey(criteriaField)) {
            return accToQueueMap.get(criteriaField);
        } else {
            int randomQueueNum = (int) (Math.random() * Integer.parseInt(getFileProperty("numberOfQueues")));
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

            String routingKey = getRoutingKeyBasedOnCreditCard(tradeIdentifiers);
            String message = tradeIdentifiers.tradeID();
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "' with routing key '" + routingKey + "'");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getRoutingKeyBasedOnCreditCard(TradeIdAndAccNum tradeIdentifiers) {
        return "cc_partition_" + getQueueMapping(tradeIdentifiers);
    }

    public static void checkRetryCountAndManageDLQ(Trade trade, LinkedBlockingDeque<String> tradeIdQueue) throws InterruptedException {
        if (retryCountMapping.containsKey(trade.getTradeID())) {
            retryCountMapping.put(trade.getTradeID(), retryCountMapping.get(trade.getTradeID()) - 1);
        } else {
            retryCountMapping.put(trade.getTradeID(), Integer.parseInt(getFileProperty("retryCount")) - 1);
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
