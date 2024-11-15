package io.reactivestax.repo.jdbc;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class JDBCJournalEntryRepoTest {

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCJournalEntryRepo instance1 = JDBCJournalEntryRepo.getInstance();
        JDBCJournalEntryRepo instance2 = JDBCJournalEntryRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCJournalEntryRepo> getInstance = JDBCJournalEntryRepo::getInstance;

        // Get two instances
        JDBCJournalEntryRepo instance1 = executorService.submit(getInstance).get();
        JDBCJournalEntryRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }
}
