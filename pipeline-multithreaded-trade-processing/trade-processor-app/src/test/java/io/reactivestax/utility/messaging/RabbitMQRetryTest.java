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

/*
1. Failed to init RabbitMQ DLX Exchange
2. Failed to getResponse from ThreadLocal
3. readMessageRetryCount first time
4. readMessageRetryCount next time
5. retryCount more than maxRetry, add to DLQ
      Test DLQ data
6. retryCount less than maxRetry
      6.1 Republish to Retry Exchange - Test if the retry queue received the message
      6.2 Republished message will come back to the Main Queue for processing - test this
      6.3 when this message comes back the retry count should be incremented by 1
*/

    @Test
    public void retryMessageFailedToInitializeDLXExchangeTest(){

    }

    @Test
    public void retryMessageFailedToGetResponseFromThreadLocalTest(){

    }

    

}
