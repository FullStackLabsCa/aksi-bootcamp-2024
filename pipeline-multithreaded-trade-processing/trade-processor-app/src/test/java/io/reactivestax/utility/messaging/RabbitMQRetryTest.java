package io.reactivestax.utility.messaging;

 import io.reactivestax.factory.BeanFactory;
 import io.reactivestax.model.Trade;
 import io.reactivestax.utility.ApplicationPropertyUtils;
 import io.reactivestax.utility.exceptions.RabbitMQException;
 import io.reactivestax.utility.messaging.rabbitmq.RabbitMQRetry;
 import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;
 import org.junit.After;
 import org.junit.Test;
 import org.mockito.MockedStatic;
 import org.mockito.Mockito;

 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;
 import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
 import static org.junit.Assert.*;

public class RabbitMQRetryTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @After
    public void cleanUp() {
        RabbitMQUtils.getInstance().closeRabbitMQChannel();
    }

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
    public void retryMessageFailedToGetChannelFromRabbitConnectionTest(){
        System.setOut(new PrintStream(outputStreamCaptor));
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)){
            MessageRetry<Trade> messageRetry;
            mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");
            messageRetry = BeanFactory.getMessageRetryer();
            assertThrows(RabbitMQException.class, () ->  messageRetry.retryMessage(Trade.builder().build()));
//            assertTrue(outputStreamCaptor.toString().contains("Unable to provide Channel from the Rabbit MQ Connection..."));
        }
        System.setOut(originalOut);
    }

    @Test
    public void retryMessageFailedToInitializeDLXExchangeTest(){
        System.setOut(new PrintStream(outputStreamCaptor));
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)){
            mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");
            mockedStatic.when(() -> getFileProperty("rabbitMQ.main.exchange.name")).thenReturn("credit_card_transactions");
            mockedStatic.when(() -> getFileProperty("rabbitMQ.main.queue0.name")).thenReturn("cc_partition_0_queue");
            mockedStatic.when(() -> getFileProperty("rabbitMQ.main.queue0.routingKey")).thenReturn("cc_partition_0");
            mockedStatic.when(() -> getFileProperty("rabbitMQ.retry.exchange.name")).thenReturn("retry_exchange");
            mockedStatic.when(() -> getFileProperty("rabbitMQ.hostName")).thenReturn("localhost");
            mockedStatic.when(() -> getFileProperty("rabbitMQ.guest")).thenReturn("guest");
            mockedStatic.when(() -> getFileProperty("rabbitMQ.pass")).thenReturn("guest");

            MessageRetry<Trade> messageRetry = BeanFactory.getMessageRetryer();
            assertThrows(RabbitMQException.class, () -> messageRetry.retryMessage(Trade.builder().build()));
            assertTrue(outputStreamCaptor.toString().contains("Error Initializing RabbitMQ Retry...."));
        }
        System.setOut(originalOut);
    }

}
