package io.reactivestax.factory;

import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.jupiter.api.Assertions.*;

class BeanFactoryTest {

    @AfterEach
    void cleanUp() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    void testGetTransactionUtilSingleThread_JDBC(){
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
    void testGetTransactionUtilSingleThread_Hibernate(){
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
    void testGetTransactionUtilSingleThread_InvalidPersistenceTech(){
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("invalidTech");

            assertThrows(InvalidPersistenceTechException.class, BeanFactory::getTransactionUtil);
        }
    }

    @Test
    void testGetTransactionUtilMultiThread_JDBC() throws ExecutionException, InterruptedException {
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("jdbc");

            Callable<TransactionUtil> getTransactionUtil = BeanFactory::getTransactionUtil;

            FutureTask<TransactionUtil> futureTask1 = new FutureTask<>(getTransactionUtil);
            FutureTask<TransactionUtil> futureTask2 = new FutureTask<>(getTransactionUtil);

            new Thread(futureTask1).start();
            new Thread(futureTask2).start();

            TransactionUtil transactionUtil1 = futureTask1.get();
            TransactionUtil transactionUtil2 = futureTask2.get();

            // Check not Null
            assertNotNull(transactionUtil1);
            assertNotNull(transactionUtil2);

            // Instance Verification
            assertFalse(transactionUtil1 instanceof HibernateUtils);
            assertFalse(transactionUtil2 instanceof HibernateUtils);
            assertInstanceOf(JDBCUtils.class, transactionUtil1);
            assertInstanceOf(JDBCUtils.class, transactionUtil2);

            // Singleton Verification
            TransactionUtil jdbcTransactionUtil = JDBCUtils.getInstance();
            assertEquals(transactionUtil1, jdbcTransactionUtil);
            assertEquals(transactionUtil2, jdbcTransactionUtil);
            assertEquals(transactionUtil1, transactionUtil2);
        }
    }

//    private void processWithMocking(String propertyName, String value){
//        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
//            mockedStatic.when(() -> getFileProperty(propertyName)).thenReturn(value);
//
//            process()
//        }
//    }

    @Test
    void testGetTransactionUtilMultiThread_Hibernate() throws ExecutionException, InterruptedException {
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("persistence.technology")).thenReturn("invalid");


            FutureTask<TransactionUtil> futureTask1 = new FutureTask<>(BeanFactory::getTransactionUtil);
            FutureTask<TransactionUtil> futureTask2 = new FutureTask<>(BeanFactory::getTransactionUtil);

            new Thread(futureTask1).start();
            new Thread(futureTask2).start();

            TransactionUtil transactionUtil1 = futureTask1.get();
            TransactionUtil transactionUtil2 = futureTask2.get();

            // Check not Null
            assertNotNull(transactionUtil1);
            assertNotNull(transactionUtil2);

            // Instance Verification
            assertFalse(transactionUtil1 instanceof JDBCUtils);
            assertFalse(transactionUtil2 instanceof JDBCUtils);
            assertInstanceOf(HibernateUtils.class, transactionUtil1);
            assertInstanceOf(HibernateUtils.class, transactionUtil2);

            // Singleton Verification
            TransactionUtil hibernateTransactionUtil = HibernateUtils.getInstance();
            assertEquals(transactionUtil1, hibernateTransactionUtil);
            assertEquals(transactionUtil2, hibernateTransactionUtil);
            assertEquals(transactionUtil1, transactionUtil2);
        }
    }
}
