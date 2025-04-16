package io.reactivestax.factory;

import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.utility.exceptions.InvalidMessagingTechnologyException;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import io.reactivestax.utility.messaging.KafkaMessageSender;
import io.reactivestax.utility.messaging.kafka.KafkaSender;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;

public class BeanFactory {

    private static final String PERSISTENCE_TECHNOLOGY = "persistence.technology";
    private static final String MESSAGING_TECHNOLOGY = "messaging.technology";

    private BeanFactory() {
    }

    private static final String JDBC_PERSISTENCE_TECH = "jdbc";
    private static final String HIBERNATE_PERSISTENCE_TECH = "hibernate";
    private static final String KAFKA_STREAMING_TECH = "kafka";


    public static RawPayloadRepo getRawPayloadRepo() {
        RawPayloadRepo rawPayloadRepo;

        if(JDBC_PERSISTENCE_TECH.equals(getFileProperty(PERSISTENCE_TECHNOLOGY))){
            rawPayloadRepo = JDBCRawPayloadRepo.getInstance();
        } else if (HIBERNATE_PERSISTENCE_TECH.equals(getFileProperty(PERSISTENCE_TECHNOLOGY))){
            rawPayloadRepo = HibernateRawPayloadRepo.getInstance();
        } else {
            throw new InvalidPersistenceTechException();
        }

        return rawPayloadRepo;
    }

    public static KafkaMessageSender<TradeIdAndAccNum, String> getMessageSender() {
        KafkaMessageSender<TradeIdAndAccNum, String> messageSender;

        if (KAFKA_STREAMING_TECH.equals(getFileProperty(MESSAGING_TECHNOLOGY))) {
            messageSender = KafkaSender.getInstance();
        } else {
            throw new InvalidMessagingTechnologyException();
        }

        return messageSender;
    }

}
