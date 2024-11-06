package io.reactivestax.utility.messaging;

import io.reactivestax.utility.messaging.rabbitmq.RabbitMQRetry;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class RabbitMQRetryTest {
    @Test
    public void getInstanceSingleThreadTest(){
        // Get two instances
        RabbitMQRetry instance1 = RabbitMQRetry.getInstance();
        RabbitMQRetry instance2 = RabbitMQRetry.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<RabbitMQRetry> getInstance = RabbitMQRetry::getInstance;

        // Get two instances
        RabbitMQRetry instance1 = executorService.submit(getInstance).get();
        RabbitMQRetry instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }
}
