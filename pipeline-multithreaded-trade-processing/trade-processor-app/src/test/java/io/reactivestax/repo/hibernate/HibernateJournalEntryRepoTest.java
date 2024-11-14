package io.reactivestax.repo.hibernate;

import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.exceptions.PositionUpdateForJournalEntryFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class HibernateJournalEntryRepoTest {

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getInstanceSingleThreadTest(){
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
    public void writeTradeToJournalEntryTableSizeTest(){
        // Get the size of table before writing
            // should be 0
        // Write Trade to Journal Entry Table
        // Get the size of table after writing
            // should be 1
    }

    @Test
    public void writeTradeToJournalEntryTableDataTest(){
        // Get the data of table before writing
            // should be null
        // Write Trade to Journal Entry Table
        // Get the data of table after writing
            // match with the inserted data
    }

    @Test
    public void writeTradeToJournalEntryFailedTest(){
        assertThrows(WriteToJournalEntryFailed.class, () -> HibernateJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(null));
    }

    @Test
    public void updateJournalEntryForPositionUpdateStatusSuccessfulTest(){
        // create trade
        // Insert Data into the database
            // Check the posted Status - it should be non-posted
        // call updateJournalEntryForPositionUpdate
            // the posted status now will be posted
    }

    @Test
    public void updateJournalEntryForPositionUpdateStatusFailedTest(){
        assertThrows(PositionUpdateForJournalEntryFailed.class, () -> HibernateJournalEntryRepo.getInstance().updateJournalEntryForPositionUpdateStatus(null));
    }

}
