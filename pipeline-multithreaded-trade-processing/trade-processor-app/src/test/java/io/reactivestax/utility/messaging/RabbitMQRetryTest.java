package io.reactivestax.utility.messaging;

 import io.reactivestax.factory.BeanFactory;
 import io.reactivestax.model.Trade;
 import io.reactivestax.utility.ApplicationPropertyUtils;
 import io.reactivestax.utility.exceptions.RabbitMQException;
 import io.reactivestax.utility.messaging.rabbitmq.RabbitMQRetry;
 import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import org.mockito.*;

 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.lang.reflect.Method;
 import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
 import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

public class RabbitMQRetryTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Spy
    private RabbitMQRetry rabbitMQRetry = RabbitMQRetry.getInstance();

    @Spy
    private RabbitMQUtils rabbitMQUtils = RabbitMQUtils.getInstance();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void cleanUp(){
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
3. readMessageRetryCount first time #TODO
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
        try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class);){

            mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");

            MessageRetry<Trade> messageRetryer = this.rabbitMQRetry;
            Trade trade = Trade.builder().build();

            assertThrows(RabbitMQException.class, () ->  messageRetryer.retryMessage(trade));
            assertTrue(outputStreamCaptor.toString().contains("Unable to provide Channel from the Rabbit MQ Connection..."));
            verify(messageRetryer, times(1)).retryMessage(any());
        }
        System.setOut(originalOut);
    }

    @Test
    public void retryMessageFailedToInitializeDLXExchangeTest() throws IOException {
        RabbitMQUtils.getInstance().closeRabbitMQConnection();

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

            MessageRetry<Trade> messageRetry = rabbitMQRetry;
            Trade trade = Trade.builder().build();
            assertThrows(RabbitMQException.class, () -> messageRetry.retryMessage(trade));
            assertTrue(outputStreamCaptor.toString().contains("Error Initializing RabbitMQ Retry...."));
        }
        System.setOut(originalOut);
    }

}
