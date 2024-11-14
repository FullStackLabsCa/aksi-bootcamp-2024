package io.reactivestax.utility.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.exceptions.RabbitMQException;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQMessageProvider;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQRetry;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
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

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        RabbitMQUtils.getInstance().closeRabbitMQConnection();
    }

    @After
    public void cleanUp() {
        RabbitMQUtils.getInstance().clearThreadResponse();
        RabbitMQUtils.getInstance().clearRabbitMQMessageProvider();
        RabbitMQUtils.getInstance().closeRabbitMQChannel();
    }

    @Test
    public void getInstance_SingleThreadTest() {
        // Get two instances
        RabbitMQRetry instance1 = RabbitMQRetry.getInstance();
        RabbitMQRetry instance2 = RabbitMQRetry.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstance_MultiThreadTest() throws ExecutionException, InterruptedException {
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
4. readMessageRetryCount Nth time
5. retryCount more than maxRetry, add to DLQ
      Test DLQ data
6. retryCount less than maxRetry
      6.1 Republish to Retry Exchange - Test if the retry queue received the message
      6.2 Republished message will come back to the Main Queue for processing - test this
      6.3 when this message comes back the retry count should be incremented by 1
*/

    @Test
    public void retryMessage_FailedToGetChannelFromRabbitConnectionTest() {
        System.setOut(new PrintStream(outputStreamCaptor));
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {

            mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");

            MessageRetry<Trade> messageRetryer = this.rabbitMQRetry;
            Trade trade = Trade.builder().build();

            assertThrows(RabbitMQException.class, () -> messageRetryer.retryMessage(trade));
            assertTrue(outputStreamCaptor.toString().contains("Unable to provide Channel from the Rabbit MQ Connection..."));
            verify(messageRetryer, times(1)).retryMessage(any());
        }
        System.setOut(originalOut);
    }

    @Test
    public void retryMessage_FailedToInitializeDLXExchangeTest() {
        System.setOut(new PrintStream(outputStreamCaptor));
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
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

    @Test
    public void retryMessage_GetMessageRetryCountFirstTimeTest() throws IOException, InterruptedException {
        purgeRabbitMQQueue();
        System.setOut(new PrintStream(outputStreamCaptor));
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {

            // SetUp
            doMocksForSuccessfulSetup(mockedStatic);
            publishMessageInMainExchangeQueue();
            setUpThreadResponseAndMessageProvider();

            MessageRetry<Trade> messageRetry = rabbitMQRetry;
            Trade trade = Trade.builder()
                    .tradeID("akshat-singla-test")
                    .build();

            // Act
            messageRetry.retryMessage(trade);

            // Check
            assertTrue(outputStreamCaptor.toString().contains("Message retried. Retry count: 1"));
            assertFalse(outputStreamCaptor.toString().contains("Message retried. Retry count: 2"));
            assertFalse(outputStreamCaptor.toString().contains("Max retries reached. Message sent to DLQ."));

        }
        System.setOut(originalOut);
    }

    @Test
    public void retryMessage_GetMessageRetryCountNthTimeTest() throws IOException, InterruptedException {
        purgeRabbitMQQueue();
        System.setOut(new PrintStream(outputStreamCaptor));
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {

            // SetUp
            doMocksForSuccessfulSetup(mockedStatic);
            publishMessageInMainExchangeQueue();
            setUpThreadResponseAndMessageProvider();

            MessageRetry<Trade> messageRetry = rabbitMQRetry;
            Trade trade = Trade.builder()
                    .tradeID("akshat-singla-test")
                    .build();

            // Act
            messageRetry.retryMessage(trade);
                // Read the message from the published queue
                // Call Retry on it again
            if (getMessageFromQueue().isPresent()) messageRetry.retryMessage(trade);

            // Check
            assertTrue(outputStreamCaptor.toString().contains("Message retried. Retry count: 2"));
            assertFalse(outputStreamCaptor.toString().contains("Message retried. Retry count: 3"));
            assertFalse(outputStreamCaptor.toString().contains("Max retries reached. Message sent to DLQ."));

        }
        System.setOut(originalOut);
    }

    @Test
    public void retryMessage_RetryCountMoreThanMaxRetryTest() throws IOException, InterruptedException {
        purgeRabbitMQQueue();
        System.setOut(new PrintStream(outputStreamCaptor));
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {

            // SetUp
            doMocksForSuccessfulSetup(mockedStatic);
            publishMessageInMainExchangeQueue();
            setUpThreadResponseAndMessageProvider();

            MessageRetry<Trade> messageRetry = rabbitMQRetry;
            Trade trade = Trade.builder()
                    .tradeID("akshat-singla-test")
                    .build();

            // Act
            messageRetry.retryMessage(trade);
            for (int i = 0; i <= Integer.parseInt(getFileProperty("retry.count")) - 1; i++) {
                if (getMessageFromQueue().isPresent()) messageRetry.retryMessage(trade);
            }

            // Check
            assertTrue(outputStreamCaptor.toString().contains("Message retried. Retry count: 1"));
            assertTrue(outputStreamCaptor.toString().contains("Message retried. Retry count: 2"));
            assertTrue(outputStreamCaptor.toString().contains("Max retries reached. Message sent to DLQ."));
        }
        System.setOut(originalOut);
    }

//    Try this out if it makes sense retryMessage_RetryCountLessThanMaxRetry_PublishToRetryQueueTest
//    retryMessage_RetryCountLessThanMaxRetry_MessageFromRetryQueueToMainQueue_Test

    private static void doMocksForSuccessfulSetup(MockedStatic<ApplicationPropertyUtils> mockedStatic) {
        mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.main.exchange.name")).thenReturn("credit_card_transactions");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.main.queue0.name")).thenReturn("cc_partition_0_queue");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.main.queue0.routingKey")).thenReturn("cc_partition_0");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.retry.exchange.name")).thenReturn("retry_exchange");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.hostName")).thenReturn("localhost");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.guest")).thenReturn("guest");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.pass")).thenReturn("guest");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.dlx.exchange.name")).thenReturn("dlx_exchange");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.dlx.queue.name")).thenReturn("dlx_queue");
        mockedStatic.when(() -> getFileProperty("rabbitMQ.dlx.routingKey")).thenReturn("dlx_routing_key");
        mockedStatic.when(() -> getFileProperty("retry.count")).thenReturn("3");
    }

    private void purgeRabbitMQQueue() throws IOException, InterruptedException {
        Thread.sleep(1000);
        RabbitMQUtils.getInstance().getRabbitMQChannel().queuePurge(getFileProperty("rabbitMQ.main.queue0.name"));
    }

    private void publishMessageInMainExchangeQueue() throws IOException {
        // Setup RabbitMQ Producer
        Channel channel = RabbitMQUtils.getInstance().getRabbitMQChannel();
        channel.exchangeDeclare(getFileProperty("rabbitMQ.main.exchange.name"), "direct");

        // Produce some data in the relevant queue
        String message = "akshat-test-data";
        RabbitMQUtils.getInstance().getRabbitMQChannel().basicPublish(getFileProperty("rabbitMQ.main.exchange.name"), "cc_partition_0", null, message.getBytes(StandardCharsets.UTF_8));
    }

    private static void setUpThreadResponseAndMessageProvider() throws IOException, InterruptedException {
        MessageProvider messageProvider = BeanFactory.getMessageProvider(0);
        GetResponse response = RabbitMQUtils.getInstance().getRabbitMQChannel().basicGet(((RabbitMQMessageProvider) messageProvider).getMainQueueName(), false);
        RabbitMQUtils.getInstance().setThreadResponse(response);
        Thread.sleep(1000);
    }

    private Optional<String> getMessageFromQueue() {
        MessageReceiver<String> messageReceiver = BeanFactory.getMessageReceiver();
        MessageProvider messageProvider = BeanFactory.getMessageProvider(0);
        return messageReceiver.receiveMessage(messageProvider);
    }

}
