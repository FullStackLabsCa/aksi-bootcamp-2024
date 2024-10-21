package io.reactivestax.factory;

import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.hibernate.HibernateJournalEntryRepo;
import io.reactivestax.repo.hibernate.HibernatePositionsRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.SecuritiesReferenceRepo;
import io.reactivestax.repo.jdbc.JDBCJournalEntryRepo;
import io.reactivestax.repo.jdbc.JDBCPositionsRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCSecuritiesReferenceRepo;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.InvalidMessagingTechnologyException;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import io.reactivestax.utility.messaging.MessageReceiver;
import io.reactivestax.utility.messaging.MessageRetry;
import io.reactivestax.utility.messaging.MessageSender;
import io.reactivestax.utility.messaging.inmemory.InMemoryReceiver;
import io.reactivestax.utility.messaging.inmemory.InMemoryRetry;
import io.reactivestax.utility.messaging.inmemory.InMemorySender;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQReceiver;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQRetry;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQSender;

import static io.reactivestax.utility.MultiThreadTradeProcessorUtility.getFileProperty;

public class BeanFactory {

    private BeanFactory() {
    }

    private static final String JDBC_PERSISTENCE_TECH = "jdbc";
    private static final String HIBERNATE_PERSISTENCE_TECH = "hibernate";
    private static final String RABBIT_MQ_QUEUE_TECH = "rabbitmq";
    private static final String IN_MEMORY_QUEUE_TECH = "in-memory";

    public static TransactionUtil getTransactionUtil(){
        TransactionUtil transactionUtil;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            transactionUtil = JDBCUtils.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            transactionUtil = HibernateUtils.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return transactionUtil;
    }

    public static RawPayloadRepo getRawPayloadRepo() {
        RawPayloadRepo rawPayloadRepo;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            rawPayloadRepo = JDBCRawPayloadRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            rawPayloadRepo = HibernateRawPayloadRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return rawPayloadRepo;
    }

    public static JournalEntryRepo getJournalEntryRepo() {
        JournalEntryRepo journalEntryRepo;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            journalEntryRepo = JDBCJournalEntryRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            journalEntryRepo = HibernateJournalEntryRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return journalEntryRepo;
    }

    public static PositionsRepo getPositionsRepo() {
        PositionsRepo positionsRepo;

        if(getFileProperty("persistence.technology").equals(JDBC_PERSISTENCE_TECH)){
            positionsRepo = JDBCPositionsRepo.getInstance();
        } else if (getFileProperty("persistence.technology").equals(HIBERNATE_PERSISTENCE_TECH)){
            positionsRepo = HibernatePositionsRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return positionsRepo;
    }

    public static SecuritiesReferenceRepo getSecuritiesReferenceRepo() {
        SecuritiesReferenceRepo securitiesReferenceRepo;

        securitiesReferenceRepo = JDBCSecuritiesReferenceRepo.getInstance();

        return securitiesReferenceRepo;
    }

    public static MessageSender<TradeIdAndAccNum> getMessageSender() {
        MessageSender<TradeIdAndAccNum> messageSender;

        if(getFileProperty("messaging.technology").equals(RABBIT_MQ_QUEUE_TECH)){
            messageSender = RabbitMQSender.getInstance();
        } else if (getFileProperty("messaging.technology").equals(IN_MEMORY_QUEUE_TECH)){
            messageSender = InMemorySender.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageSender;
    }

    public static MessageReceiver<String> getMessageReceiver(){
        MessageReceiver<String> messageReceiver;

        if(getFileProperty("messaging.technology").equals(RABBIT_MQ_QUEUE_TECH)){
            messageReceiver = RabbitMQReceiver.getInstance();
        } else if (getFileProperty("messaging.technology").equals(IN_MEMORY_QUEUE_TECH)){
            messageReceiver = InMemoryReceiver.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageReceiver;
    }

    public static MessageRetry<Trade> getMessageRetryer(){
        MessageRetry<Trade> messageRetryer;

        if(getFileProperty("messaging.technology").equals(RABBIT_MQ_QUEUE_TECH)){
            messageRetryer = RabbitMQRetry.getInstance();
        } else if (getFileProperty("messaging.technology").equals(IN_MEMORY_QUEUE_TECH)){
            messageRetryer = InMemoryRetry.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageRetryer;
    }
}
