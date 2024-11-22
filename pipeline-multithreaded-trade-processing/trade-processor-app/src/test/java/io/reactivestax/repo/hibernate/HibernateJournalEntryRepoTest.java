package io.reactivestax.repo.hibernate;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.JournalEntry;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.UpdatePositionStatusInJournalEntryFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HibernateJournalEntryRepoTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanUp(){
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
    void getInstanceSingleThreadTest() {
        // Get two instances
        HibernateJournalEntryRepo instance1 = HibernateJournalEntryRepo.getInstance();
        HibernateJournalEntryRepo instance2 = HibernateJournalEntryRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
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
    void writeTradeToJournalEntryTableSizeTest() throws WriteToJournalEntryFailed {
        // Get the size of table before writing
        // should be 0
        String hql = "SELECT COUNT(e) FROM JournalEntry e";
        Query<Long> query = HibernateUtils.getInstance().getConnection().createQuery(hql, Long.class);
        long countBeforeInsertion = query.uniqueResult();

        assertEquals(0, countBeforeInsertion);

        // Create Trade
        Trade trade = TestDataProvider.goodTradeSupplier.get();

        // Write Trade to Journal Entry Table
        HibernateUtils.getInstance().startTransaction();
        HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        // Get the size of table after writing
        // should be 1
        Query<Long> queryAfterCreation = HibernateUtils.getInstance().getConnection().createQuery(hql, Long.class);
        long countAfterInsertion = queryAfterCreation.uniqueResult();

        assertEquals(1, countAfterInsertion);
    }

    @Test
    void writeTradeToJournalEntryTableDataTest() throws WriteToJournalEntryFailed {
        // Get the data of table before writing
        // should be null
        String hql = "SELECT e FROM JournalEntry e";
        Query<JournalEntry> query = HibernateUtils.getInstance().getConnection().createQuery(hql, JournalEntry.class);
        List<JournalEntry> entriesBeforeInsertion = query.getResultList();

        assertEquals(0, entriesBeforeInsertion.size());

        // Create Trade
        Trade trade = TestDataProvider.goodTradeSupplier.get();

        // Write Trade to Journal Entry Table
        HibernateUtils.getInstance().startTransaction();
        HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        // Get the data of table after writing
        // match with the inserted data
        Query<JournalEntry> queryAfterCreation = HibernateUtils.getInstance().getConnection().createQuery(hql, JournalEntry.class);
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
    void writeTradeToJournalEntryFailedTest() {
        assertThrows(WriteToJournalEntryFailed.class, () -> HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(null));
    }

    @Test
    void updatePositionPostedStatusInJournalEntrySuccessfulTest() throws WriteToJournalEntryFailed, UpdatePositionStatusInJournalEntryFailed {
        // create trade
        Trade trade = TestDataProvider.goodTradeSupplier.get();

        // Insert Data into the database
        HibernateUtils.getInstance().startTransaction();
        HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        // Check the posted Status - it should be non-posted
        String hql = "SELECT e FROM JournalEntry e";
        Query<JournalEntry> query = HibernateUtils.getInstance().getConnection().createQuery(hql, JournalEntry.class);
        List<JournalEntry> entriesInJournalEntryTable = query.getResultList();

        JournalEntry journalEntryFromDB = entriesInJournalEntryTable.get(0);
        assertEquals("Non Posted", journalEntryFromDB.getPositionPostedStatus());

        // call updateJournalEntryForPositionUpdate
        HibernateUtils.getInstance().startTransaction();
        HibernateJournalEntryRepo.getInstance().updatePositionPostedStatusInJournalEntry(trade);
        HibernateUtils.getInstance().commitTransaction();

        // the posted status now will be posted
        Query<JournalEntry> queryAfterUpdate = HibernateUtils.getInstance().getConnection().createQuery(hql, JournalEntry.class);
        List<JournalEntry> entriesInJournalEntryTableAfterUpdate = queryAfterUpdate.getResultList();

        JournalEntry journalEntryFromDBAfterUpdate = entriesInJournalEntryTableAfterUpdate.get(0);
        assertEquals("Posted", journalEntryFromDBAfterUpdate.getPositionPostedStatus());
    }

    @Test
    void updatePositionPostedStatusInJournalEntryFailedTest() {
        assertThrows(UpdatePositionStatusInJournalEntryFailed.class, () -> HibernateJournalEntryRepo.getInstance().updatePositionPostedStatusInJournalEntry(null));
    }

}
