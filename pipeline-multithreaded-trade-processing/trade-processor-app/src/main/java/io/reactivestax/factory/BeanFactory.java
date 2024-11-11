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

    private static final String RABBIT_MQ_QUEUE_TECH = "rabbitmq";
    private static final String IN_MEMORY_QUEUE_TECH = "in-memory";
    private static final Map<String, Callable<Object>> jdbcTech = new HashMap<>();
    private static final Map<String, Callable<Object>> hibernateTech = new HashMap<>();
    private static final Map<String, Callable<Object>> rabbitMQTech = new HashMap<>();
    private static final Map<String, Map<String, Callable<Object>>> mapOfTech = new HashMap<>();

    static {
        jdbcTech.put("transactionUtil", JDBCUtils::getInstance);
        jdbcTech.put("rawPayloadRepo", JDBCRawPayloadRepo::getInstance);
        jdbcTech.put("journalEntryRepo", JDBCJournalEntryRepo::getInstance);
        jdbcTech.put("positionRepo", JDBCPositionsRepo::getInstance);
        jdbcTech.put("securitiesReferenceRepo", JDBCSecuritiesReferenceRepo::getInstance);

        hibernateTech.put("transactionUtil", HibernateUtils::getInstance);
        hibernateTech.put("rawPayloadRepo", HibernateRawPayloadRepo::getInstance);
        hibernateTech.put("journalEntryRepo", HibernateJournalEntryRepo::getInstance);
        hibernateTech.put("positionRepo", HibernatePositionsRepo::getInstance);
        hibernateTech.put("securitiesReferenceRepo", HibernateSecuritiesReferenceRepo::getInstance);

        rabbitMQTech.put("messageReceiver", RabbitMQReceiver::getInstance);
        rabbitMQTech.put("messageRetryer", RabbitMQRetry::getInstance);

        mapOfTech.put("jdbc", jdbcTech);
        mapOfTech.put("hibernate", hibernateTech);
        mapOfTech.put("rabbitmq", rabbitMQTech);
    }

    private static Optional<Object> callSafely(Callable<Object> value) {
        try{
            return Optional.of(value.call());
        } catch (Exception e){
            return Optional.empty();
        }
    }

    public static TransactionUtil getTransactionUtil(){
        return mapOfTech.entrySet().stream()
                .filter(techMap -> techMap.getKey().equals(getFileProperty("persistence.technology")))
                .map(Map.Entry::getValue)
                .flatMap(techMapValues -> techMapValues.entrySet().stream())
                .filter(resource -> resource.getKey().equals("transactionUtil"))
                .map(resource -> callSafely(resource.getValue()))
                .flatMap(Optional::stream)
                .map(TransactionUtil.class::cast)
                .findFirst()
                .orElseThrow(InvalidPersistenceTechException::new);
    }

    public static RawPayloadRepo getRawPayloadRepo() {
        return mapOfTech.entrySet().stream()
                .filter(techMap -> techMap.getKey().equals(getFileProperty("persistence.technology")))
                .map(Map.Entry::getValue)
                .flatMap(techMapValues -> techMapValues.entrySet().stream())
                .filter(resource -> resource.getKey().equals("rawPayloadRepo"))
                .map(resource -> callSafely(resource.getValue()))
                .flatMap(Optional::stream)
                .map(RawPayloadRepo.class::cast)
                .findFirst()
                .orElseThrow(InvalidPersistenceTechException::new);
    }

    public static JournalEntryRepo getJournalEntryRepo() {
        return mapOfTech.entrySet().stream()
                .filter(techMap -> techMap.getKey().equals(getFileProperty("persistence.technology")))
                .map(Map.Entry::getValue)
                .flatMap(techMapValues -> techMapValues.entrySet().stream())
                .filter(resource -> resource.getKey().equals("journalEntryRepo"))
                .map(resource -> callSafely(resource.getValue()))
                .flatMap(Optional::stream)
                .map(JournalEntryRepo.class::cast)
                .findFirst()
                .orElseThrow(InvalidPersistenceTechException::new);
    }

    public static PositionsRepo getPositionsRepo() {
        return mapOfTech.entrySet().stream()
                .filter(techMap -> techMap.getKey().equals(getFileProperty("persistence.technology")))
                .map(Map.Entry::getValue)
                .flatMap(techMapValues -> techMapValues.entrySet().stream())
                .filter(resource -> resource.getKey().equals("positionRepo"))
                .map(resource -> callSafely(resource.getValue()))
                .flatMap(Optional::stream)
                .map(PositionsRepo.class::cast)
                .findFirst()
                .orElseThrow(InvalidPersistenceTechException::new);
    }

    public static SecuritiesReferenceRepo getSecuritiesReferenceRepo() {
        return mapOfTech.entrySet().stream()
                .filter(techMap -> techMap.getKey().equals(getFileProperty("persistence.technology")))
                .map(Map.Entry::getValue)
                .flatMap(techMapValues -> techMapValues.entrySet().stream())
                .filter(resource -> resource.getKey().equals("securitiesReferenceRepo"))
                .map(resource -> callSafely(resource.getValue()))
                .flatMap(Optional::stream)
                .map(SecuritiesReferenceRepo.class::cast)
                .findFirst()
                .orElseThrow(InvalidPersistenceTechException::new);
    }

    public static MessageReceiver<String> getMessageReceiver(){
        MessageReceiver<String> messageReceiver;

        if(getFileProperty("messaging.technology").equals(RABBIT_MQ_QUEUE_TECH)){
            messageReceiver = RabbitMQReceiver.getInstance();
        } else if (getFileProperty("messaging.technology").equals(IN_MEMORY_QUEUE_TECH)){
            throw new NoLongerSupportedException();
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
            throw new InvalidMessagingTechnologyException();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageRetryer;
    }

    public static MessageProvider getMessageProvider(int index){
        MessageProvider messageProvider;

        if(getFileProperty("messaging.technology").equals(RABBIT_MQ_QUEUE_TECH)){
            RabbitMQMessageProvider rabbitMQMessageProvider = new RabbitMQMessageProvider();
            rabbitMQMessageProvider.setMainExchangeName(getFileProperty("rabbitMQ.main.exchange.name"));
            rabbitMQMessageProvider.setMainQueueName(getFileProperty("rabbitMQ.main.queue"+index+".name"));
            rabbitMQMessageProvider.setMainQueueRoutingKey(getFileProperty("rabbitMQ.main.queue"+index+".routingKey"));
            rabbitMQMessageProvider.setRetryExchangeName(getFileProperty("rabbitMQ.retry.exchange.name"));
            rabbitMQMessageProvider.setRetryQueueName(getFileProperty("rabbitMQ.main.queue"+index+".name")+"_retry");
            rabbitMQMessageProvider.setRetryQueueRoutingKey(getFileProperty("rabbitMQ.main.queue"+index+".routingKey")+"_retry");

            RabbitMQUtils.getInstance().setRabbitMQMessageProvider(rabbitMQMessageProvider);
            messageProvider = rabbitMQMessageProvider;

        } else if (getFileProperty("messaging.technology").equals(IN_MEMORY_QUEUE_TECH)){
            throw new InvalidMessagingTechnologyException();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageProvider;
    }
}
