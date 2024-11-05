package io.reactivestax.utility.messaging;

import io.reactivestax.utility.messaging.rabbitmq.RabbitMQReceiver;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class RabbitMQReceiverTest {

    @Test
    public void getInstanceSingleThreadTest(){
        // Get two instances
        RabbitMQReceiver instance1 = RabbitMQReceiver.getInstance();
        RabbitMQReceiver instance2 = RabbitMQReceiver.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<RabbitMQReceiver> getInstance = RabbitMQReceiver::getInstance;

        // Get two instances
        RabbitMQReceiver instance1 = executorService.submit(getInstance).get();
        RabbitMQReceiver instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }
}
