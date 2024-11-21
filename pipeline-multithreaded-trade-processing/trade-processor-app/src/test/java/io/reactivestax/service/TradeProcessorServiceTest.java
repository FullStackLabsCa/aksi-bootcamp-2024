package io.reactivestax.service;


import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;


class TradeProcessorServiceTest {

    @Test
    void getInstanceSingleThreadTest(){
        // Get two instances
        TradeProcessorService instance1 = TradeProcessorService.getInstance();
        TradeProcessorService instance2 = TradeProcessorService.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<TradeProcessorService> getInstance = TradeProcessorService::getInstance;

        // Get two instances
        TradeProcessorService instance1 = executorService.submit(getInstance).get();
        TradeProcessorService instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

//runTradeProcessorTest
    /* Mocked Test

     */

    /* Integration Test

     */

//getTradeIdTest
    /* Mocked Test

     */

    /* Integration Test

     */

//readPayloadTest
    /* Mocked Test

     */

    /* Integration Test

     */

//readPayloadFromRawDatabaseTest
    /* Mocked Test

     */

    /* Integration Test

     */

//validatePayloadAndCreateChunkTest
    /* Mocked Test

     */

    /* Integration Test

     */

//convertStringToSQLDateTest
    /* Mocked Test

     */

    /* Integration Test

     */

//processTradeTest
    /* Mocked Test

     */

    /* Integration Test

     */

//validateBusinessLogicTest
    /* Mocked Test

     */

    /* Integration Test

     */

//updateSecurityLookupStatusInRawPayloadTest
    /* Mocked Test

     */

    /* Integration Test

     */

//updateJournalEntryAndPositionsTest
    /* Mocked Test

     */

    /* Integration Test

     */

//writeToJournalTableTest
    /* Mocked Test

     */

    /* Integration Test

     */

//writeToPositionTableTest
    /* Mocked Test

     */

    /* Integration Test

     */

//updateJEPostedStatusInRawPayloadTest
    /* Mocked Test

     */

    /* Integration Test

     */

//UpdatePositionPostedStatusInJETest
    /* Mocked Test
    -   Anything Happens
            updatePositionPostedStatusInJournalEntry is called Once
            getPersistenceBean is called once
    -   Assert Throws
            updatePositionPostedStatusInJournalEntry throws UpdateException
     */

    /* Integration Test
    -   null trade
    -   invalid trade
    -   ExceptionCase for PositionStatusUpdateInJournalEntry
     */
}
