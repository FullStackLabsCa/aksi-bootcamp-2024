package io.reactivestax.repo;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.RawPayload;
import io.reactivestax.repo.hibernate.HibernateRawPayloadRepo;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HibernateRawPayloadRepoTest {

    @Spy
    private HibernateUtils hibernateUtilsSpy;

    @InjectMocks
    private HibernateRawPayloadRepo hibernateRawPayloadRepo;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        ApplicationPropertyUtils.readPropertiesFile("src/test/resources/test.application.properties");
    }

    @AfterEach
    void cleanUp(){
        try {
            HibernateUtils.getInstance().startTransaction();
            String sql = "delete from RawPayload";
            jakarta.persistence.Query query = HibernateUtils.getInstance().getConnection().createQuery(sql);
            query.executeUpdate();
            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            HibernateUtils.getInstance().rollbackTransaction();
        }

        ApplicationPropertyUtils.resetProperties();
    }

    @Test
    void getInstanceSingleThreadTest() {
        // Get two instances
        HibernateRawPayloadRepo instance1 = HibernateRawPayloadRepo.getInstance();
        HibernateRawPayloadRepo instance2 = HibernateRawPayloadRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<HibernateRawPayloadRepo> getInstance = HibernateRawPayloadRepo::getInstance;

        // Get two instances
        HibernateRawPayloadRepo instance1 = executorService.submit(getInstance).get();
        HibernateRawPayloadRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void writeToRawPayloadTest(){
        long sizeOfTableBeforeInsertion = getSizeOfTable();

        String tradeID = TestDataProvider.validTradeIdSupplier.get();
        String payload = TestDataProvider.validTradePayloadSupplier.get();
        String validStatus = "Valid";
        HibernateRawPayloadRepo.getInstance().writeToRawPayloadTable(TestDataProvider.validRawPayloadSupplier.get());

        long sizeOfTableAfterInsertion = getSizeOfTable();
        List<RawPayload> entriesInTableAfterInsertion = getEntriesInTable();

        assertEquals(sizeOfTableBeforeInsertion + 1, sizeOfTableAfterInsertion);
        assertEquals(tradeID, entriesInTableAfterInsertion.get(0).getTradeID());
        assertEquals(payload, entriesInTableAfterInsertion.get(0).getPayload());
        assertEquals(validStatus, entriesInTableAfterInsertion.get(0).getStatus());
    }

    private long getSizeOfTable(){
        String hql = "SELECT COUNT(e) FROM RawPayload e";
        Query<Long> query = HibernateUtils.getInstance().getConnection().createQuery(hql, Long.class);
        return query.uniqueResult();
    }

    private List<RawPayload> getEntriesInTable(){
        String hql = "SELECT e FROM RawPayload e";
        Query<RawPayload> query = HibernateUtils.getInstance().getConnection().createQuery(hql, RawPayload.class);
        return query.getResultList();
    }

    @Test
    void writeToRawPayloadExceptionTest(){
        Session session = HibernateUtils.getInstance().getConnection();
        try(MockedStatic<HibernateUtils> hibernateUtilsMockedStatic = Mockito.mockStatic(HibernateUtils.class)){
            //Setup
            hibernateUtilsMockedStatic.when(HibernateUtils::getInstance).thenReturn(hibernateUtilsSpy);
            doReturn(session).when(hibernateUtilsSpy).getConnection();
            doAnswer(invocationOnMock -> {throw new Exception();}).when(hibernateUtilsSpy).startTransaction();

            //Action
            hibernateRawPayloadRepo.writeToRawPayloadTable(TestDataProvider.validRawPayloadSupplier.get());

            //Assertion
            verify(hibernateUtilsSpy, times(0)).commitTransaction();
            verify(hibernateUtilsSpy, times(1)).rollbackTransaction();
        }
    }
}
