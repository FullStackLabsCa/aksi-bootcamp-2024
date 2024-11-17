package io.reactivestax.repo.hibernate;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.RawPayload;
import io.reactivestax.utility.database.HibernateUtils;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class HibernateRawPayloadRepoTest {

    @After
    public void cleanUp(){
        try {
            HibernateUtils.getInstance().startTransaction();
            String sql = "delete from RawPayload";
            jakarta.persistence.Query query = HibernateUtils.getInstance().getConnection().createQuery(sql);
            query.executeUpdate();
            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            HibernateUtils.getInstance().rollbackTransaction();
        }
    }

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        HibernateRawPayloadRepo instance1 = HibernateRawPayloadRepo.getInstance();
        HibernateRawPayloadRepo instance2 = HibernateRawPayloadRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
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
    public void readPayloadFromRawPayload_tradeIdExists_Test(){
        String tradePayload = TestDataProvider.validTradePayloadSupplier.get();

        // Insert the Trade with Payload into the DB
        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();
        try {
            RawPayload rawPayload = RawPayload.builder()
                    .tradeID("TDB_00000001")
                    .status("Valid")
                    .payload(tradePayload)
                    .lookupStatus("Non Posted")
                    .postedStatus("Non Posted")
                    .build();

            session.persist(rawPayload);

            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            HibernateUtils.getInstance().rollbackTransaction();
        }

        // Read the payload for the same trade ID, it will be the same as that of Inserted
        Optional<String> payloadReadFromRawPayloadTable = HibernateRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("TDB_00000001");
        assertEquals(tradePayload, payloadReadFromRawPayloadTable.get());
    }

    @Test
    public void readPayloadFromRawPayload_TradeIdDoesNotExists_Test(){
        // Read the Payload for a tradeId, It will be Optional.empty()
        Optional<String> payloadReadFromRawPayloadTable = HibernateRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("test-trade");
        assertEquals(Optional.empty(), payloadReadFromRawPayloadTable);
    }

    /*
    Update Security LookUp Status in RawPayloads Table
    -   Check Before
    -   Update ( Valid # TODO 3 and Invalid # TODO 4)
    -   Check After - Should Match the Updated
    - Exception Occured # TODO 5
     */

    /*
    Update Journal Entry Status in Raw Payload Table
    -   Check Before
    -   Update Posted
    -   Check After Should be posted # TODO 6
    - Exception Occured # TODO 7
     */
}
