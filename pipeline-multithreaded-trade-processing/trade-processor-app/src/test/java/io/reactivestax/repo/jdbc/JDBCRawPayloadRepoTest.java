package io.reactivestax.repo.jdbc;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class JDBCRawPayloadRepoTest {

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCRawPayloadRepo instance1 = JDBCRawPayloadRepo.getInstance();
        JDBCRawPayloadRepo instance2 = JDBCRawPayloadRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCRawPayloadRepo> getInstance = JDBCRawPayloadRepo::getInstance;

        // Get two instances
        JDBCRawPayloadRepo instance1 = executorService.submit(getInstance).get();
        JDBCRawPayloadRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }
}
