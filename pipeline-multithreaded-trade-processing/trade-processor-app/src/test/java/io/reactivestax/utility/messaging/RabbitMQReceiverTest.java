package io.reactivestax.utility.messaging;

import com.rabbitmq.client.Channel;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQReceiver;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.Assert.*;

public class RabbitMQReceiverTest {
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Spy
    private RabbitMQReceiver rabbitMQReceiver = RabbitMQReceiver.getInstance();

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void cleanUp(){
        RabbitMQUtils.getInstance().clearThreadResponse();
        RabbitMQUtils.getInstance().clearRabbitMQMessageProvider();
        RabbitMQUtils.getInstance().closeRabbitMQChannel();
    }

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

    @Test
    public void receiveMessageWithDataAvailableInQueueTest(){
        try {
            // Setup RabbitMQ Producer
            Channel channel = RabbitMQUtils.getInstance().getRabbitMQChannel();
            channel.exchangeDeclare(getFileProperty("rabbitMQ.main.exchange.name"), "direct");

            // Produce some data in the relevant queue
            String message = "akshat-test-data";
            RabbitMQUtils.getInstance().getRabbitMQChannel().basicPublish(getFileProperty("rabbitMQ.main.exchange.name"), "cc_partition_0", null, message.getBytes(StandardCharsets.UTF_8));

            MessageReceiver<String> messageReceiver;
            MessageProvider messageProvider;

            // Mock the Consumer to read from the Queue
            try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)){
                mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");
                mockedStatic.when(() -> getFileProperty("rabbitMQ.main.exchange.name")).thenReturn("credit_card_transactions");
                mockedStatic.when(() -> getFileProperty("rabbitMQ.main.queue0.name")).thenReturn("cc_partition_0_queue");
                mockedStatic.when(() -> getFileProperty("rabbitMQ.main.queue0.routingKey")).thenReturn("cc_partition_0");
                mockedStatic.when(() -> getFileProperty("rabbitMQ.retry.exchange.name")).thenReturn("retry_exchange");

                messageReceiver = BeanFactory.getMessageReceiver();
                messageProvider = BeanFactory.getMessageProvider(0);
            }

            // Verify the Message Received
            Optional<String> messageReceived = messageReceiver.receiveMessage(messageProvider);

            messageReceived.ifPresent(s -> assertEquals(message, s));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void receiveMessageErrorInRabbitMQSetupTest(){
            // Mock the Consumer to read from the Queue
            try(MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)){
                MessageReceiver<String> messageReceiver;
                MessageProvider messageProvider;
                mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");
                messageReceiver = BeanFactory.getMessageReceiver();
                messageProvider = BeanFactory.getMessageProvider(0);
                assertTrue(messageReceiver.receiveMessage(messageProvider).isEmpty());
            }
    }

    @Test
    public void rabbitMQThrowExceptionTest(){
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

            MessageReceiver<String> messageReceiver = rabbitMQReceiver;
            messageReceiver.receiveMessage(null);
            assertTrue(outputStreamCaptor.toString().contains("Error Initializing RabbitMQ Receiver Main...."));
        }
        System.setOut(originalOut);
    }
}
