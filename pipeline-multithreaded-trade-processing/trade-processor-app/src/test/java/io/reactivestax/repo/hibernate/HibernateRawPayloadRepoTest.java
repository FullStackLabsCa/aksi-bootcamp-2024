package io.reactivestax.repo.hibernate;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.RawPayload;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.UpdateJournalEntryStatusInRawPayloadFailed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class HibernateRawPayloadRepoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

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
       insertIntoRawPayloadTable("TDB_00000001", tradePayload);

        // Read the payload for the same trade ID, it will be the same as that of Inserted
        Optional<String> payloadReadFromRawPayloadTable = HibernateRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("TDB_00000001");
        assertEquals(tradePayload, payloadReadFromRawPayloadTable.get());
    }

    private void insertIntoRawPayloadTable(String tradeId, String payload){
        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();
        try {
            RawPayload rawPayload = RawPayload.builder()
                    .tradeID(tradeId)
                    .status("Valid")
                    .payload(payload)
                    .lookupStatus("Not Posted")
                    .postedStatus("Not Posted")
                    .build();

            session.persist(rawPayload);

            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            HibernateUtils.getInstance().rollbackTransaction();
        }
    }

    @Test
    public void readPayloadFromRawPayload_TradeIdDoesNotExists_Test(){
        // Read the Payload for a tradeId, It will be Optional.empty()
        Optional<String> payloadReadFromRawPayloadTable = HibernateRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("test-trade");
        assertEquals(Optional.empty(), payloadReadFromRawPayloadTable);
    }

    @ParameterizedTest
    @MethodSource("provideLookUpStatusForTest")
    void updateSecurityLookUpStatusTest(String lookupStatus){
        Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();
        insertIntoRawPayloadTable("TDB_00000001", TestDataProvider.validTradePayloadSupplier.get());

        HibernateRawPayloadRepo.getInstance().updateSecurityLookupStatusInRawPayloadsTable(trade, lookupStatus);
        RawPayload rawPayloadAfterUpdate = readPayloadFromRawPayload("TDB_00000001");

        if ("Valid".equals(lookupStatus))
        assertEquals("Succeeded", rawPayloadAfterUpdate.getLookupStatus());
        else
            assertEquals("Failed", rawPayloadAfterUpdate.getLookupStatus());

    }

    private RawPayload readPayloadFromRawPayload(String tradeID){
        Session session = HibernateUtils.getInstance().getConnection();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<RawPayload> query = builder.createQuery(RawPayload.class);
        Root<RawPayload> root = query.from(RawPayload.class);
        query.select(root).where(builder.equal(root.get("tradeID"), tradeID));
        List<RawPayload> students = session.createQuery(query).getResultList();
        return students.get(0);
    }

    static Stream<Arguments> provideLookUpStatusForTest() {
        return Stream.of(
                Arguments.of("Valid"),
                Arguments.of("Invalid")
        );
    }

    @Test
    public void updateSecurityLookUpStatusFailedTest(){
        System.setOut(new PrintStream(outputStreamCaptor));

        HibernateRawPayloadRepo.getInstance().updateSecurityLookupStatusInRawPayloadsTable(null, null);
        assertTrue(outputStreamCaptor.toString().contains("Failed to Update Security Lookup Status in Raw-Payload Table"));

        System.setOut(originalOut);
    }

    @Test
    public void updateJournalEntryStatusTest() throws UpdateJournalEntryStatusInRawPayloadFailed {
        Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();
        insertIntoRawPayloadTable("TDB_00000001", TestDataProvider.validTradePayloadSupplier.get());

        RawPayload rawPayloadBeforeUpdate = readPayloadFromRawPayload("TDB_00000001");
        assertEquals("Not Posted", rawPayloadBeforeUpdate.getPostedStatus());

        HibernateUtils.getInstance().startTransaction();
        HibernateRawPayloadRepo.getInstance().updateJournalEntryStatusInRawPayloadsTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        RawPayload rawPayloadAfterUpdate = readPayloadFromRawPayload("TDB_00000001");

        assertEquals("Posted", rawPayloadAfterUpdate.getPostedStatus());

    }

    @Test
    public void updateJournalEntryStatusFailedTest() {
        assertThrows(UpdateJournalEntryStatusInRawPayloadFailed.class, () -> HibernateRawPayloadRepo.getInstance().updateJournalEntryStatusInRawPayloadsTable(null));
    }
}
