package io.reactivestax.repo.hibernate;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.PositionUpdateForJournalEntryFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.Assert.*;

public class HibernateJournalEntryRepoTest {

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void cleanUp(){
        try {
            HibernateUtils.getInstance().startTransaction();
            String sql = "delete from JournalEntry";
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
        HibernateJournalEntryRepo instance1 = HibernateJournalEntryRepo.getInstance();
        HibernateJournalEntryRepo instance2 = HibernateJournalEntryRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<HibernateJournalEntryRepo> getInstance = HibernateJournalEntryRepo::getInstance;

        // Get two instances
        HibernateJournalEntryRepo instance1 = executorService.submit(getInstance).get();
        HibernateJournalEntryRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void writeTradeToJournalEntryTableSizeTest() throws WriteToJournalEntryFailed {
        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();

        // Get the size of table before writing
        // should be 0
        String hql = "SELECT COUNT(e) FROM JournalEntry e";
        Query<Long> query = session.createQuery(hql, Long.class);
        long countBeforeInsertion = query.uniqueResult();

        assertEquals(0, countBeforeInsertion);

        // Create Trade
        Trade trade = Trade.builder()
                .tradeID("TD123")
                .accountNumber("123")
                .activity("BUY")
                .price(0.0)
                .transactionTime(new java.sql.Date(2024))
                .cusip("TSLA")
                .quantity(10)
                .build();

        // Write Trade to Journal Entry Table
        HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);

        // Get the size of table after writing
        // should be 1
        Query<Long> queryAfterCreation = session.createQuery(hql, Long.class);
        long countAfterInsertion = queryAfterCreation.uniqueResult();

        assertEquals(1, countAfterInsertion);
    }

    @Test
    public void writeTradeToJournalEntryTableDataTest() throws WriteToJournalEntryFailed {
        Session session = HibernateUtils.getInstance().getConnection();
        HibernateUtils.getInstance().startTransaction();

        // Get the data of table before writing
        // should be null
        String hql = "SELECT e FROM JournalEntry e";
        Query<JournalEntry> query = session.createQuery(hql, JournalEntry.class);
        List<JournalEntry> entriesBeforeInsertion = query.getResultList();

        assertEquals(0, entriesBeforeInsertion.size());

        // Create Trade
        Trade trade = Trade.builder()
                .tradeID("TD123")
                .accountNumber("123")
                .activity("BUY")
                .price(0.0)
                .transactionTime(new java.sql.Date(2024))
                .cusip("TSLA")
                .quantity(10)
                .build();

        // Write Trade to Journal Entry Table
        HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);

        // Get the data of table after writing
        // match with the inserted data
        Query<JournalEntry> queryAfterCreation = session.createQuery(hql, JournalEntry.class);
        List<JournalEntry> entriesAfterInsertion = queryAfterCreation.getResultList();

        JournalEntry expectedJournalEntry = JournalEntry.builder()
                .accountNumber(trade.getAccountNumber())
                .activity(trade.getActivity())
                .positionPostedStatus("Non Posted")
                .quantity(trade.getQuantity())
                .securityID(157001093)
                .tradeExecutionTime(trade.getTransactionTime())
                .tradeID(trade.getTradeID())
                .build();

        assertEquals(expectedJournalEntry, entriesAfterInsertion.get(0));
        assertEquals(1, entriesAfterInsertion.size());
    }

    @Test
    public void writeTradeToJournalEntryFailedTest() {
        assertThrows(WriteToJournalEntryFailed.class, () -> HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(null));
    }

    @Test
    public void updateJournalEntryForPositionUpdateStatusSuccessfulTest() {
        // create trade
        // Insert Data into the database
        // Check the posted Status - it should be non-posted
        // call updateJournalEntryForPositionUpdate
        // the posted status now will be posted
    }

    @Test
    public void updateJournalEntryForPositionUpdateStatusFailedTest() {
        assertThrows(PositionUpdateForJournalEntryFailed.class, () -> HibernateJournalEntryRepo.getInstance().updateJournalEntryForPositionUpdateStatus(null));
    }

}
