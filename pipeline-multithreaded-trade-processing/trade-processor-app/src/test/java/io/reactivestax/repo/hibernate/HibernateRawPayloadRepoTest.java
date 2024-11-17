package io.reactivestax.repo.hibernate;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class HibernateRawPayloadRepoTest {

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
}
