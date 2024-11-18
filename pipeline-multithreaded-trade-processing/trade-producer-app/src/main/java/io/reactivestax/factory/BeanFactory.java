package io.reactivestax.factory;

import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.utility.exceptions.InvalidMessagingTechnologyException;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import io.reactivestax.utility.messaging.MessageSender;
import io.reactivestax.utility.messaging.inmemory.InMemorySender;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQSender;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;

public class BeanFactory {

    private BeanFactory() {
    }

    private static final String JDBC_PERSISTENCE_TECH = "jdbc";
    private static final String HIBERNATE_PERSISTENCE_TECH = "hibernate";
    private static final String RABBIT_MQ_QUEUE_TECH = "rabbitmq";
    private static final String IN_MEMORY_QUEUE_TECH = "in-memory";


    public static RawPayloadRepo getRawPayloadRepo() {
        RawPayloadRepo rawPayloadRepo;

        if(JDBC_PERSISTENCE_TECH.equals(getFileProperty("persistence.technology"))){
            rawPayloadRepo = JDBCRawPayloadRepo.getInstance();
        } else if (HIBERNATE_PERSISTENCE_TECH.equals(getFileProperty("persistence.technology"))){
            rawPayloadRepo = HibernateRawPayloadRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return rawPayloadRepo;
    }

    public static MessageSender<TradeIdAndAccNum> getMessageSender() {
        MessageSender<TradeIdAndAccNum> messageSender;

        if(RABBIT_MQ_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            messageSender = RabbitMQSender.getInstance();
        } else if (IN_MEMORY_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            messageSender = InMemorySender.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageSender;
    }

}
