package io.reactivestax.factory;

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

//    private void processWithMocking(String propertyName, String value){
//        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
//            mockedStatic.when(() -> getFileProperty(propertyName)).thenReturn(value);
//
//            process()
//        }
//    }

}
