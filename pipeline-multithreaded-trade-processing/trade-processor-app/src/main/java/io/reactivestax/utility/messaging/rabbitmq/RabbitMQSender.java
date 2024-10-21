package io.reactivestax.utility.messaging.rabbitmq;

import io.reactivestax.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.messaging.MessageSender;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class RabbitMQSender implements MessageSender<TradeIdAndAccNum> {
    private static RabbitMQSender instance;
    private static final ConcurrentHashMap<String, Integer> accToQueueMap = new ConcurrentHashMap<>();

    private RabbitMQSender() {
    }

    public static synchronized RabbitMQSender  getInstance(){
        if(instance == null) instance = new RabbitMQSender();
        return instance;
    }

    @Override
    public void sendMessage(TradeIdAndAccNum tradeIdAndAccNum) {
        try {
            RabbitMQUtils.getInstance();
            Objects.requireNonNull(RabbitMQUtils.getRabbitMQChannel()).exchangeDeclare(getFileProperty("rabbitMQ.exchangeName"), "direct");

            String routingKey = getRoutingKey(tradeIdAndAccNum);
            String message = tradeIdAndAccNum.tradeID();
            RabbitMQUtils.getRabbitMQChannel().basicPublish(getFileProperty("rabbitMQ.exchangeName"), routingKey, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "' with routing key '" + routingKey + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRoutingKey(TradeIdAndAccNum tradeIdentifiers) {
        return "cc_partition_" + getQueueMapping(tradeIdentifiers);
    }

    private static int getQueueMapping(TradeIdAndAccNum tradeIdentifiers) {
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
}
