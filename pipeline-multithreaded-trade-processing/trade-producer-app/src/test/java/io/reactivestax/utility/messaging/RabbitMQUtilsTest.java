package io.reactivestax.utility.messaging;

import com.rabbitmq.client.Channel;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.exceptions.SystemInitializationException;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static io.reactivestax.utility.ApplicationPropertyUtils.getFileProperty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class RabbitMQUtilsTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Spy
    private Channel channelSpy;

    @InjectMocks
    @Spy
    private RabbitMQUtils rabbitMQUtils;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanUp() throws IOException {
        RabbitMQUtils.getInstance().closeRabbitMQConnection();
    }

    @Test
    void getInstanceSingleThreadTest(){
        // Get two instances
        RabbitMQUtils instance1 = RabbitMQUtils.getInstance();
        RabbitMQUtils instance2 = RabbitMQUtils.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
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

    @Test
    void getRabbitMQChannelMultiThreadTest() throws ExecutionException, InterruptedException {
        // Spawn multiple threads and make each of them get 2 connections
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<List<Channel>> getChannel = () -> {
            Channel channel1 = RabbitMQUtils.getInstance().getRabbitMQChannel();
            Channel channel2 = RabbitMQUtils.getInstance().getRabbitMQChannel();
            List<Channel> listOfChannels = new ArrayList<>();
            listOfChannels.add(channel1);
            listOfChannels.add(channel2);
            return listOfChannels;
        };

        List<Channel> thread1Channels = executorService.submit(getChannel).get();
        List<Channel> thread2Channels = executorService.submit(getChannel).get();

        // Check Channel Open
        assertTrue(thread1Channels.get(0).isOpen());
        assertTrue(thread1Channels.get(1).isOpen());
        assertTrue(thread2Channels.get(0).isOpen());
        assertTrue(thread2Channels.get(1).isOpen());

        // Both the connections for the same thread will be same
        assertEquals(thread1Channels.get(0).hashCode(), thread1Channels.get(1).hashCode());
        assertEquals(System.identityHashCode(thread1Channels.get(0)), System.identityHashCode(thread1Channels.get(1)));
        assertEquals(thread2Channels.get(0).hashCode(), thread2Channels.get(1).hashCode());
        assertEquals(System.identityHashCode(thread2Channels.get(0)), System.identityHashCode(thread2Channels.get(1)));

        // Two connections from any two different threads will be different
        assertNotEquals(thread1Channels.get(0).hashCode(), thread2Channels.get(0).hashCode());
        assertNotEquals(thread1Channels.get(0).hashCode(), thread2Channels.get(0).hashCode());
        assertNotEquals(System.identityHashCode(thread1Channels.get(0)), System.identityHashCode(thread2Channels.get(0)));
        assertNotEquals(thread1Channels.get(1).hashCode(), thread2Channels.get(1).hashCode());
        assertNotEquals(System.identityHashCode(thread1Channels.get(1)), System.identityHashCode(thread2Channels.get(1)));
    }

    @Test
    void failedToGetChannelFromRabbitConnectionTest() {
        System.setOut(new PrintStream(outputStreamCaptor));
        try (MockedStatic<ApplicationPropertyUtils> mockedStatic = Mockito.mockStatic(ApplicationPropertyUtils.class)) {
            mockedStatic.when(() -> getFileProperty("messaging.technology")).thenReturn("rabbitmq");

            assertThrows(SystemInitializationException.class, () -> RabbitMQUtils.getInstance().getRabbitMQChannel());
            assertTrue(outputStreamCaptor.toString().contains("Unable to provide Channel from the Rabbit MQ Connection..."));
        }
        System.setOut(originalOut);
    }

    @Test
    void closeRabbitMQSingleThreadTest(){
        Channel channel = RabbitMQUtils.getInstance().getRabbitMQChannel();
        assertTrue(channel.isOpen());
        RabbitMQUtils.getInstance().closeRabbitMQChannel();
        assertFalse(channel.isOpen());
    }

    @Test
    void closeRabbitMQMultiThreadTest() throws ExecutionException, InterruptedException {
        Callable<Boolean> closeChannelAndGetOpenStatus = () -> {
            Channel channel = RabbitMQUtils.getInstance().getRabbitMQChannel();
            RabbitMQUtils.getInstance().closeRabbitMQChannel();
            return channel.isOpen();
        };

        Channel channelMainThread = RabbitMQUtils.getInstance().getRabbitMQChannel();
        assertTrue(channelMainThread.isOpen());

        FutureTask<Boolean> futureTaskSecondaryThread = new FutureTask<>(closeChannelAndGetOpenStatus);
        new Thread(futureTaskSecondaryThread).start();
        boolean secondaryThreadActivityStatus = futureTaskSecondaryThread.get();
        assertFalse(secondaryThreadActivityStatus);

        assertTrue(channelMainThread.isOpen());

        RabbitMQUtils.getInstance().closeRabbitMQChannel();
        assertFalse(channelMainThread.isOpen());
    }

    @Test
    void closeRabbitMQChannel_IOExceptionTest() throws IOException, TimeoutException {
        RabbitMQUtils.getInstance().getRabbitMQChannel();
        try(MockedStatic<RabbitMQUtils> rabbitMQUtilsMockedStatic = Mockito.mockStatic(RabbitMQUtils.class)){
            rabbitMQUtilsMockedStatic.when(RabbitMQUtils::getInstance).thenReturn(rabbitMQUtils);

            doReturn(channelSpy).when(rabbitMQUtils).getRabbitMQChannel();
            doThrow(IOException.class).when(channelSpy).close();

            assertThrows(SystemInitializationException.class, () -> rabbitMQUtils.closeRabbitMQChannel());
        }
    }

    @Test
    void closeRabbitMQChannel_TimeoutExceptionTest() throws IOException, TimeoutException {
        RabbitMQUtils.getInstance().getRabbitMQChannel();
        try(MockedStatic<RabbitMQUtils> rabbitMQUtilsMockedStatic = Mockito.mockStatic(RabbitMQUtils.class)){
            rabbitMQUtilsMockedStatic.when(RabbitMQUtils::getInstance).thenReturn(rabbitMQUtils);

            doReturn(channelSpy).when(rabbitMQUtils).getRabbitMQChannel();
            doThrow(TimeoutException.class).when(channelSpy).close();

            assertThrows(SystemInitializationException.class, () -> rabbitMQUtils.closeRabbitMQChannel());
        }
    }
}
