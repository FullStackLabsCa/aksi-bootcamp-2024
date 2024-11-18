package io.reactivestax.repo.jdbc;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class JDBCSecuritiesReferenceRepoTest {

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCSecuritiesReferenceRepo instance1 = JDBCSecuritiesReferenceRepo.getInstance();
        JDBCSecuritiesReferenceRepo instance2 = JDBCSecuritiesReferenceRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCSecuritiesReferenceRepo> getInstance = JDBCSecuritiesReferenceRepo::getInstance;

        // Get two instances
        JDBCSecuritiesReferenceRepo instance1 = executorService.submit(getInstance).get();
        JDBCSecuritiesReferenceRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

}
