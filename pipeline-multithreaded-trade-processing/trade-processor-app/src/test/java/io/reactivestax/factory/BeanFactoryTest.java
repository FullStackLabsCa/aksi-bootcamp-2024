package io.reactivestax.factory;

import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.hibernate.HibernateJournalEntryRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCJournalEntryRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
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

}
