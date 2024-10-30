package io.reactivestax.utility.messaging;

import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class RabbitMQUtilsTest {

    @Test
    public void getInstanceSingleThreadTest(){
        // Get two instances
        RabbitMQUtils instance1 = RabbitMQUtils.getInstance();
        RabbitMQUtils instance2 = RabbitMQUtils.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<RabbitMQUtils> getRabbitMQUtilsInstance = RabbitMQUtils::getInstance;

        // Get two instances
        RabbitMQUtils instance1 = executorService.submit(getRabbitMQUtilsInstance).get();
        RabbitMQUtils instance2 = executorService.submit(getRabbitMQUtilsInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }
}
