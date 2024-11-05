package io.reactivestax.factory;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.SecuritiesReferenceRepo;
import io.reactivestax.repo.hibernate.HibernateJournalEntryRepo;
import io.reactivestax.repo.hibernate.HibernatePositionsRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCJournalEntryRepo;
import io.reactivestax.repo.jdbc.JDBCPositionsRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCSecuritiesReferenceRepo;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.InvalidMessagingTechnologyException;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import io.reactivestax.utility.exceptions.NoLongerSupportedException;
import io.reactivestax.utility.messaging.MessageReceiver;
import io.reactivestax.utility.messaging.MessageRetry;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQReceiver;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQRetry;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.jupiter.api.Assertions.*;

class BeanFactoryTest {


    @Test
    void testGetTransactionUtil_JDBC(){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("jdbc");

            TransactionUtil jdbcTransactionUtil = JDBCUtils.getInstance();
            TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();

            // Check not Null
            assertNotNull(transactionUtil);

            // Instance Verification
            assertFalse(transactionUtil instanceof HibernateUtils);
            assertInstanceOf(JDBCUtils.class, transactionUtil);

            // Singleton Verification
            assertEquals(transactionUtil, jdbcTransactionUtil);
        }
    }

    @Test
    void testGetTransactionUtil_Hibernate(){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("hibernate");

            TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();

            TransactionUtil hibernateTransactionUtil = HibernateUtils.getInstance();

            // Check Not Null
            assertNotNull(transactionUtil);

            // Instance Verification
            assertFalse(transactionUtil instanceof JDBCUtils);
            assertInstanceOf(HibernateUtils.class, transactionUtil);

            // Singleton Verification
            assertEquals(transactionUtil, hibernateTransactionUtil);
        }
    }

    @Test
    void testGetTransactionUtil_InvalidPersistenceTech(){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("invalidTech");

            assertThrows(InvalidPersistenceTechException.class, BeanFactory::getTransactionUtil);
        }
    }

    private void withMockedProperty(String propertyName, String value, Runnable functionToExecute){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty(propertyName)).thenReturn(value);
            functionToExecute.run();
        }
    }

    @Test
    void testGetTransactionUtilWithParametrizedMocking_JDBC(){
        Runnable test = () -> {
            TransactionUtil jdbcTransactionUtil = JDBCUtils.getInstance();
            TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();

            // Check not Null
            assertNotNull(transactionUtil);

            // Instance Verification
            assertFalse(transactionUtil instanceof HibernateUtils);
            assertInstanceOf(JDBCUtils.class, transactionUtil);

            // Singleton Verification
            assertEquals(transactionUtil, jdbcTransactionUtil);
        };

        withMockedProperty("persistence.technology","jdbc", test);
    }

    @Test
    void testGetTransactionUtilWithParametrizedMocking_Hibernate(){
        Runnable test = () -> {
            TransactionUtil transactionUtil = BeanFactory.getTransactionUtil();

            TransactionUtil hibernateTransactionUtil = HibernateUtils.getInstance();

            assertNotNull(transactionUtil);
            // Instance Verification
            assertFalse(transactionUtil instanceof JDBCUtils);
            assertInstanceOf(HibernateUtils.class, transactionUtil);
            // Singleton Verification
            assertEquals(transactionUtil, hibernateTransactionUtil);
        };

        withMockedProperty("persistence.technology","hibernate", test);
    }

    @Test
    void testGetRawPayloadRepo_JDBC(){
        Runnable test = () -> {
            RawPayloadRepo rawPayloadRepo = BeanFactory.getRawPayloadRepo();
            RawPayloadRepo jdbcRawPayloadRepo = JDBCRawPayloadRepo.getInstance();

            assertNotNull(rawPayloadRepo);
            assertFalse(rawPayloadRepo instanceof HibernateRawPayloadRepo);
            assertInstanceOf(JDBCRawPayloadRepo.class, rawPayloadRepo);
            // Singleton Verification
            assertEquals(rawPayloadRepo, jdbcRawPayloadRepo);
        };

        withMockedProperty("persistence.technology","jdbc", test);
    }

    @Test
    void testGetRawPayloadRepo_Hibernate(){
        Runnable test = () -> {
            RawPayloadRepo rawPayloadRepo = BeanFactory.getRawPayloadRepo();
            RawPayloadRepo hibernateRawPayloadRepo = HibernateRawPayloadRepo.getInstance();

            assertNotNull(rawPayloadRepo);
            assertFalse(rawPayloadRepo instanceof JDBCRawPayloadRepo);
            assertInstanceOf(HibernateRawPayloadRepo.class, rawPayloadRepo);
            // Singleton Verification
            assertEquals(rawPayloadRepo, hibernateRawPayloadRepo);
        };

        withMockedProperty("persistence.technology","hibernate", test);
    }

    @Test
    void testGetRawPayloadRepo_InvalidTech(){
        Runnable test = () -> {
            assertThrows(InvalidPersistenceTechException.class, BeanFactory::getRawPayloadRepo);
        };

        withMockedProperty("persistence.technology","invalid",test);
    }

    @Test
    void testGetJournalEntryRepo_JDBC(){
        Runnable test = () -> {
            JournalEntryRepo journalEntryRepo = BeanFactory.getJournalEntryRepo();
            JournalEntryRepo jdbcJournalEntryRepo = JDBCJournalEntryRepo.getInstance();

            assertNotNull(journalEntryRepo);
            assertFalse(journalEntryRepo instanceof HibernateJournalEntryRepo);
            assertInstanceOf(JDBCJournalEntryRepo.class, journalEntryRepo);
            // Singleton Verification
            assertEquals(journalEntryRepo, jdbcJournalEntryRepo);
        };

        withMockedProperty("persistence.technology","jdbc", test);
    }

    @Test
    void testGetJournalEntryRepo_Hibernate(){
        Runnable test = () -> {
            JournalEntryRepo journalEntryRepo = BeanFactory.getJournalEntryRepo();
            JournalEntryRepo hibernateJournalEntryRepo = HibernateJournalEntryRepo.getInstance();

            assertNotNull(journalEntryRepo);
            assertFalse(journalEntryRepo instanceof JDBCJournalEntryRepo);
            assertInstanceOf(HibernateJournalEntryRepo.class, journalEntryRepo);
            // Singleton Verification
            assertEquals(journalEntryRepo, hibernateJournalEntryRepo);
        };

        withMockedProperty("persistence.technology","hibernate", test);
    }

    @Test
    void testGetJournalEntryRepo_InvalidTech(){
        Runnable test = () -> {
            assertThrows(InvalidPersistenceTechException.class, BeanFactory::getJournalEntryRepo);
        };

        withMockedProperty("persistence.technology","invalid",test);
    }

    @Test
    void testGetPositionRepo_JDBC(){
        Runnable test = () -> {
            PositionsRepo positionsRepo = BeanFactory.getPositionsRepo();
            PositionsRepo jdbcPositionsRepo = JDBCPositionsRepo.getInstance();

            assertNotNull(positionsRepo);
            assertFalse(positionsRepo instanceof HibernatePositionsRepo);
            assertInstanceOf(JDBCPositionsRepo.class, positionsRepo);
            // Singleton Verification
            assertEquals(positionsRepo, jdbcPositionsRepo);
        };

        withMockedProperty("persistence.technology","jdbc", test);
    }

    @Test
    void testGetPositionRepo_Hibernate(){
        Runnable test = () -> {
            PositionsRepo positionsRepo = BeanFactory.getPositionsRepo();
            PositionsRepo hibernatePositionsRepo = HibernatePositionsRepo.getInstance();

            assertNotNull(positionsRepo);
            assertFalse(positionsRepo instanceof JDBCPositionsRepo);
            assertInstanceOf(HibernatePositionsRepo.class, positionsRepo);
            // Singleton Verification
            assertEquals(positionsRepo, hibernatePositionsRepo);
        };

        withMockedProperty("persistence.technology","hibernate", test);
    }

    @Test
    void testGetPositionRepo_InvalidTech(){
        Runnable test = () -> {
            assertThrows(InvalidPersistenceTechException.class, BeanFactory::getPositionsRepo);
        };

        withMockedProperty("persistence.technology","invalid",test);
    }

    @Test
    void testGetSecuritiesReferenceRepo_JDBC(){
        Runnable test = () -> {
            SecuritiesReferenceRepo securitiesReferenceRepo = BeanFactory.getSecuritiesReferenceRepo();
            SecuritiesReferenceRepo jdbcSecuritiesReferenceRepo = JDBCSecuritiesReferenceRepo.getInstance();

            assertNotNull(securitiesReferenceRepo);
            assertInstanceOf(JDBCSecuritiesReferenceRepo.class, securitiesReferenceRepo);
            // Singleton Verification
            assertEquals(securitiesReferenceRepo, jdbcSecuritiesReferenceRepo);
        };

        withMockedProperty("persistence.technology","jdbc", test);
    }

    @Test
    void testGetMessageReceiver_RabbitMQ(){
        Runnable test = () -> {
            MessageReceiver<String> messageReceiver = BeanFactory.getMessageReceiver();
            MessageReceiver<String> rabbitMQReceiver = RabbitMQReceiver.getInstance();

            assertNotNull(messageReceiver);
            assertInstanceOf(RabbitMQReceiver.class, messageReceiver);
            // Singleton Verification
            assertEquals(messageReceiver, rabbitMQReceiver);
        };

        withMockedProperty("messaging.technology","rabbitmq", test);
    }

    @Test
    void testGetMessageReceiver_InMemory(){
        Runnable test = () -> {
            assertThrows(NoLongerSupportedException.class,BeanFactory::getMessageReceiver);
        };

        withMockedProperty("messaging.technology","in-memory", test);
    }

    @Test
    void testGetMessageReceiver_InvalidTech(){
        Runnable test = () -> {
            assertThrows(InvalidMessagingTechnologyException.class, BeanFactory::getMessageReceiver);
        };

        withMockedProperty("messaging.technology","invalid",test);
    }

    @Test
    void testGetMessageRetryer_RabbitMQ(){
        Runnable test = () -> {
            MessageRetry<Trade> messageReceiver = BeanFactory.getMessageRetryer();
            MessageRetry<Trade> rabbitMQReceiver = RabbitMQRetry.getInstance();

            assertNotNull(messageReceiver);
            assertInstanceOf(RabbitMQRetry.class, messageReceiver);
            // Singleton Verification
            assertEquals(messageReceiver, rabbitMQReceiver);
        };

        withMockedProperty("messaging.technology","rabbitmq", test);
    }

    @Test
    void testGetMessageRetryer_InMemory(){
        Runnable test = () -> {
            assertThrows(InvalidMessagingTechnologyException.class,BeanFactory::getMessageRetryer);
        };

        withMockedProperty("messaging.technology","in-memory", test);
    }

    @Test
    void testGetMessageRetryer_InvalidTech(){
        Runnable test = () -> {
            assertThrows(InvalidMessagingTechnologyException.class, BeanFactory::getMessageRetryer);
        };

        withMockedProperty("messaging.technology","invalid",test);
    }

}
