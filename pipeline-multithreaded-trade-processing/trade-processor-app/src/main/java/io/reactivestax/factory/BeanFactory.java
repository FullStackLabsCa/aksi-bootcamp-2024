package io.reactivestax.factory;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.hibernate.HibernateJournalEntryRepo;
import io.reactivestax.repo.hibernate.HibernatePositionsRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.SecuritiesReferenceRepo;
import io.reactivestax.repo.hibernate.HibernateSecuritiesReferenceRepo;
import io.reactivestax.repo.jdbc.JDBCJournalEntryRepo;
import io.reactivestax.repo.jdbc.JDBCPositionsRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCSecuritiesReferenceRepo;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.InvalidMessagingTechnologyException;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import io.reactivestax.utility.exceptions.NoLongerSupportedException;
import io.reactivestax.utility.messaging.MessageProvider;
import io.reactivestax.utility.messaging.MessageReceiver;
import io.reactivestax.utility.messaging.MessageRetry;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQMessageProvider;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQReceiver;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQRetry;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;

public class BeanFactory {

    private BeanFactory() {
    }

    private static final Map<String, Map<Class<?>, Callable<?>>> mapOfTech = new HashMap<>();
    private static final String RABBIT_MQ_QUEUE_TECH = "rabbitmq";
    private static final String IN_MEMORY_QUEUE_TECH = "in-memory";

    static {
        initializePersistenceTechnologies();
    }

    private static void initializePersistenceTechnologies() {
        mapOfTech.put("jdbc", initJDBCTech());
        mapOfTech.put("hibernate", initHibernateTech());
    }

    private static Map<Class<?>, Callable<?>> initJDBCTech(){
        Map<Class<?>, Callable<?>> jdbcTech = new HashMap<>();
        jdbcTech.put(TransactionUtil.class, JDBCUtils::getInstance);
        jdbcTech.put(RawPayloadRepo.class, JDBCRawPayloadRepo::getInstance);
        jdbcTech.put(JournalEntryRepo.class, JDBCJournalEntryRepo::getInstance);
        jdbcTech.put(PositionsRepo.class, JDBCPositionsRepo::getInstance);
        jdbcTech.put(SecuritiesReferenceRepo.class, JDBCSecuritiesReferenceRepo::getInstance);
        return jdbcTech;
    }

    private static Map<Class<?>, Callable<?>> initHibernateTech(){
        Map<Class<?>, Callable<?>> hibernateTech = new HashMap<>();
        hibernateTech.put(TransactionUtil.class, HibernateUtils::getInstance);
        hibernateTech.put(RawPayloadRepo.class, HibernateRawPayloadRepo::getInstance);
        hibernateTech.put(JournalEntryRepo.class, HibernateJournalEntryRepo::getInstance);
        hibernateTech.put(PositionsRepo.class, HibernatePositionsRepo::getInstance);
        hibernateTech.put(SecuritiesReferenceRepo.class, HibernateSecuritiesReferenceRepo::getInstance);
        return hibernateTech;
    }

    private static Optional<Object> callSafely(Callable<?> value) {
        try{
            return Optional.of(value.call());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static <T> T getPersistenceBean(Class<T> classType){
        return Optional.ofNullable(mapOfTech.get(getFileProperty("persistence.technology")))
                .map(techMap -> techMap.get(classType))
                .flatMap(BeanFactory::callSafely)
                .map(classType::cast)
                .orElseThrow(InvalidPersistenceTechException::new);
    }

    public static MessageReceiver<String> getMessageReceiver(){
        MessageReceiver<String> messageReceiver;

        if(RABBIT_MQ_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            messageReceiver = RabbitMQReceiver.getInstance();
        } else if (IN_MEMORY_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            throw new NoLongerSupportedException();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageReceiver;
    }

    public static MessageRetry<Trade> getMessageRetryer(){
        MessageRetry<Trade> messageRetryer;

        if(RABBIT_MQ_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            messageRetryer = RabbitMQRetry.getInstance();
        } else if (IN_MEMORY_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            throw new InvalidMessagingTechnologyException();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageRetryer;
    }

    public static MessageProvider getMessageProvider(int index){
        MessageProvider messageProvider;

        if(RABBIT_MQ_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            RabbitMQMessageProvider rabbitMQMessageProvider = new RabbitMQMessageProvider();
            rabbitMQMessageProvider.setMainExchangeName(getFileProperty("rabbitMQ.main.exchange.name"));
            rabbitMQMessageProvider.setMainQueueName(getFileProperty("rabbitMQ.main.queue"+index+".name"));
            rabbitMQMessageProvider.setMainQueueRoutingKey(getFileProperty("rabbitMQ.main.queue"+index+".routingKey"));
            rabbitMQMessageProvider.setRetryExchangeName(getFileProperty("rabbitMQ.retry.exchange.name"));
            rabbitMQMessageProvider.setRetryQueueName(getFileProperty("rabbitMQ.main.queue"+index+".name")+"_retry");
            rabbitMQMessageProvider.setRetryQueueRoutingKey(getFileProperty("rabbitMQ.main.queue"+index+".routingKey")+"_retry");

            RabbitMQUtils.getInstance().setRabbitMQMessageProvider(rabbitMQMessageProvider);
            messageProvider = rabbitMQMessageProvider;

        } else if (IN_MEMORY_QUEUE_TECH.equals(getFileProperty("messaging.technology"))){
            throw new InvalidMessagingTechnologyException();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageProvider;
    }
}
