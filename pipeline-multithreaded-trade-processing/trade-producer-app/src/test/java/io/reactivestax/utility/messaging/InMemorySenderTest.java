package io.reactivestax.utility.messaging;

import io.reactivestax.utility.messaging.inmemory.InMemorySender;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class InMemorySenderTest {
    @Test
    public void getInstance_SingleThreadTest() {
        // Get two instances
        InMemorySender instance1 = InMemorySender.getInstance();
        InMemorySender instance2 = InMemorySender.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstance_MultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<InMemorySender> getInstance = InMemorySender::getInstance;

        // Get two instances
        InMemorySender instance1 = executorService.submit(getInstance).get();
        InMemorySender instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }
}
