package io.reactivestax.utility.messaging.rabbitmq;

import com.rabbitmq.client.Channel;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.messaging.MessageSender;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;

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
            Channel channel = RabbitMQUtils.getInstance().getRabbitMQChannel();
            channel.exchangeDeclare(getFileProperty("rabbitMQ.main.exchange.name"), "direct");

            String routingKey = getRoutingKey(tradeIdAndAccNum);
            String message = tradeIdAndAccNum.tradeID();

            RabbitMQUtils.getInstance().getRabbitMQChannel().basicPublish(getFileProperty("rabbitMQ.main.exchange.name"), routingKey, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "' with routing key '" + routingKey + "'");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRoutingKey(TradeIdAndAccNum tradeIdentifiers) {
        return "cc_partition_" + getQueueMapping(tradeIdentifiers);
    }

    private static Integer getQueueMapping(TradeIdAndAccNum tradeIdentifiers) {
        String criteria = getFileProperty("trade.distribution.criteria");

        String criteriaField = criteria.equals("tradeID") ? tradeIdentifiers.tradeID() : tradeIdentifiers.accountNumber();

        return Optional.of(criteriaField)
                .filter(accToQueueMap::containsKey)
                .map(accToQueueMap::get)
                .orElseGet(() -> {
                    int randomQueueNum = (int) (Math.random() * Integer.parseInt(getFileProperty("trade.processor.queue.count")));
                    accToQueueMap.put(criteriaField, randomQueueNum);
                    return randomQueueNum;
                });
    }
}
