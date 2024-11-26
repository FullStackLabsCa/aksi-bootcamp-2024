package io.reactivestax.utility.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import io.reactivestax.utility.exceptions.NullResponseForThreadException;
import io.reactivestax.utility.exceptions.SystemInitializationException;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQMessageProvider;
import io.reactivestax.utility.messaging.rabbitmq.RabbitMQUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;


class RabbitMQUtilsTest {

    @BeforeEach
    void cleanUp() {
        RabbitMQUtils.getInstance().clearRabbitMQMessageProvider();
        RabbitMQUtils.getInstance().clearThreadResponse();
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
        assertNotEquals(System.identityHashCode(thread1Channels.get(0)), System.identityHashCode(thread2Channels.get(0)));
        assertNotEquals(thread1Channels.get(1).hashCode(), thread2Channels.get(1).hashCode());
        assertNotEquals(System.identityHashCode(thread1Channels.get(1)), System.identityHashCode(thread2Channels.get(1)));
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
    void setThreadResponseSingleThreadTest(){
        assertThrows(NullResponseForThreadException.class, RabbitMQUtils.getInstance()::getThreadResponse);
        RabbitMQUtils.getInstance().setThreadResponse(new GetResponse(null,null,null,0));
        GetResponse response = RabbitMQUtils.getInstance().getThreadResponse();
        assertNotNull(response);
        RabbitMQUtils.getInstance().clearThreadResponse();
    }

    @Test
    void getThreadResponseWithoutBeingSetSingleThreadTest(){
        assertThrows(NullResponseForThreadException.class, RabbitMQUtils.getInstance()::getThreadResponse);
    }

    @Test
    void getThreadResponseAfterBeingSetSingleThreadTest(){
        GetResponse getResponse = new GetResponse(null, null, null, 1);
        RabbitMQUtils.getInstance().setThreadResponse(getResponse);
        GetResponse responseAfterBeingSet = RabbitMQUtils.getInstance().getThreadResponse();
        assertNotNull(responseAfterBeingSet);
        RabbitMQUtils.getInstance().clearThreadResponse();
    }

    @Test
    void getThreadResponseMultiThreadTest() throws ExecutionException, InterruptedException {
        Callable<GetResponse> getResponse = () -> {
            RabbitMQUtils.getInstance().setThreadResponse(new GetResponse(null,null,null,0));
            return RabbitMQUtils.getInstance().getThreadResponse();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        GetResponse responseFromThread1 = executorService.submit(getResponse).get();
        GetResponse responseFromThread2 = executorService.submit(getResponse).get();

        assertNotEquals(responseFromThread1, responseFromThread2);

        executorService.submit(() -> RabbitMQUtils.getInstance().clearThreadResponse());
        executorService.submit(() -> RabbitMQUtils.getInstance().clearThreadResponse());

        executorService.shutdown();
    }

    @Test
    void getRabbitMQMessageProviderBeforeSetSingleThreadTest(){
        assertThrows(SystemInitializationException.class, RabbitMQUtils.getInstance()::getRabbitMQMessageProvider);
    }

    @Test
    void getRabbitMQMessageProviderMultiThreadTest(){
        // Main thread
        assertThrows(SystemInitializationException.class, RabbitMQUtils.getInstance()::getRabbitMQMessageProvider);

        new Thread(() -> RabbitMQUtils.getInstance().setRabbitMQMessageProvider(new RabbitMQMessageProvider())).start();

        // Main Thread will still get the exception
        assertThrows(SystemInitializationException.class, RabbitMQUtils.getInstance()::getRabbitMQMessageProvider);

    }

    @Test
    void rabbitMQMessageProviderSingleThreadTest(){
        assertThrows(SystemInitializationException.class, RabbitMQUtils.getInstance()::getRabbitMQMessageProvider);
        RabbitMQUtils.getInstance().setRabbitMQMessageProvider(new RabbitMQMessageProvider());
        assertNotNull(RabbitMQUtils.getInstance().getRabbitMQMessageProvider());
        RabbitMQUtils.getInstance().clearRabbitMQMessageProvider();
    }

    @Test
    void rabbitMQMessageProviderMultiThreadTest(){
        Runnable checkGetRabbitMQMessageProvider = () -> {
            assertThrows(SystemInitializationException.class, RabbitMQUtils.getInstance()::getRabbitMQMessageProvider);
            RabbitMQUtils.getInstance().setRabbitMQMessageProvider(new RabbitMQMessageProvider());
            assertNotNull(RabbitMQUtils.getInstance().getRabbitMQMessageProvider());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            RabbitMQUtils.getInstance().clearRabbitMQMessageProvider();
        };

        new Thread(checkGetRabbitMQMessageProvider).start();
        new Thread(checkGetRabbitMQMessageProvider).start();
    }
}
