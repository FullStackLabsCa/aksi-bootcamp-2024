package io.reactivestax.factory;

import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.InvalidMessagingTechnologyException;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import io.reactivestax.utility.messaging.MessageSender;
import io.reactivestax.utility.messaging.inmemory.InMemorySender;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQSender;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class BeanFactoryTest {

    @Test
    public void testGetRawPayloadRepo_JDBC(){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("jdbc");

            RawPayloadRepo jdbcTransactionUtil = JDBCRawPayloadRepo.getInstance();
            RawPayloadRepo transactionUtil = BeanFactory.getRawPayloadRepo();

            // Check not Null
            assertNotNull(transactionUtil);

            // Instance Verification
            assertFalse(transactionUtil instanceof HibernateUtils);
            assertInstanceOf(JDBCRawPayloadRepo.class, transactionUtil);

            // Singleton Verification
            assertEquals(transactionUtil, jdbcTransactionUtil);
        }
    }

    @Test
    public void testGetRawPayloadRepo_Hibernate(){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("hibernate");

            RawPayloadRepo rawPayloadRepoFromBeanFactory = BeanFactory.getRawPayloadRepo();

            RawPayloadRepo hibernateRawPayloadRepo = HibernateRawPayloadRepo.getInstance();

            // Check Not Null
            assertNotNull(rawPayloadRepoFromBeanFactory);

            // Instance Verification
            assertFalse(rawPayloadRepoFromBeanFactory instanceof JDBCUtils);
            assertInstanceOf(HibernateRawPayloadRepo.class, rawPayloadRepoFromBeanFactory);

            // Singleton Verification
            assertEquals(rawPayloadRepoFromBeanFactory, hibernateRawPayloadRepo);
        }
    }

    @Test
    public void testGetRawPayloadRepo_InvalidPersistenceTech(){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("invalidTech");

            assertThrows(InvalidPersistenceTechException.class, BeanFactory::getRawPayloadRepo);
        }
    }

    private void withMockedProperty(String propertyName, String value, Runnable functionToExecute){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty(propertyName)).thenReturn(value);
            functionToExecute.run();
        }
    }

    @Test
    public void testGetMessageReceiver_RabbitMQ(){
        Runnable test = () -> {
            MessageSender<TradeIdAndAccNum> messageReceiver = BeanFactory.getMessageSender();
            MessageSender<TradeIdAndAccNum> rabbitMQReceiver = RabbitMQSender.getInstance();

            assertNotNull(messageReceiver);
            assertInstanceOf(RabbitMQSender.class, messageReceiver);
            // Singleton Verification
            assertEquals(messageReceiver, rabbitMQReceiver);
        };

        withMockedProperty("messaging.technology","rabbitmq", test);
    }

    @Test
    public void testGetMessageReceiver_InMemory(){
        Runnable test = () -> {
            MessageSender<TradeIdAndAccNum> messageSender = BeanFactory.getMessageSender();
            MessageSender<TradeIdAndAccNum> inMemorySender = InMemorySender.getInstance();

            assertNotNull(messageSender);
            assertInstanceOf(InMemorySender.class, messageSender);
            // Singleton Verification
            assertEquals(messageSender, inMemorySender);
        };
        withMockedProperty("messaging.technology","in-memory", test);
    }

    @Test
    public void testGetMessageReceiver_InvalidTech(){
        Runnable test = () -> assertThrows(InvalidMessagingTechnologyException.class, BeanFactory::getMessageSender);

        withMockedProperty("messaging.technology","invalid",test);
    }
}
