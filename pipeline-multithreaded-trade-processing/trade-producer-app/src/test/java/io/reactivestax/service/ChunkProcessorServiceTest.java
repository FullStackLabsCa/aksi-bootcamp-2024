package io.reactivestax.service;


import io.reactivestax.TestDataProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ChunkProcessorServiceTest {

    @Test
    void getInstance_SingleThreadTest() {
        // Get two instances
        ChunkProcessorService instance1 = ChunkProcessorService.getInstance();
        ChunkProcessorService instance2 = ChunkProcessorService.getInstance();

        // Hashcode will be same
        Assertions.assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        Assertions.assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstance_MultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<ChunkProcessorService> getInstance = ChunkProcessorService::getInstance;

        // Get two instances
        ChunkProcessorService instance1 = executorService.submit(getInstance).get();
        ChunkProcessorService instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        Assertions.assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        Assertions.assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    /*
    processChunkTest
     */

    /*
    processPayloadTest
     */

    /*
    checkPayloadValidityTest
     */
    @Test
    void checkPayloadValidityTest_ValidPayload(){
        String payload = TestDataProvider.validTradePayloadSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        Assertions.assertEquals("Valid", result);
    }

    @Test
    void checkPayloadValidityTest_InvalidPayload(){
        String payload = TestDataProvider.invalidTradePayloadSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        Assertions.assertEquals("Invalid", result);
    }

    @Test
    void checkPayloadValidityTest_EmptyStringPayload(){
        String payload = TestDataProvider.emptyTradePayloadSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        Assertions.assertEquals("Invalid", result);
    }

    @Test
    void checkPayloadValidityTest_NullPayload(){
        String payload = TestDataProvider.nullTradePayloadSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        Assertions.assertEquals("Invalid", result);
    }

    /*
    getIdentifierFromPayloadTest
     */

    /*
    writePayloadToPayloadDatabaseTest
     */

    /*
    sendForProcessingTest
     */
}
