package io.reactivestax.factory;

import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.InvalidPersistenceTechException;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.jupiter.api.Assertions.*;

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
}
