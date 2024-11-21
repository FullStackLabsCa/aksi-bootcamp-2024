package io.reactivestax.service;


import io.reactivestax.TestDataProvider;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.utility.exceptions.OptimisticLockingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeProcessorServiceTest {

    @Spy
    private JournalEntryRepo journalEntryRepoSpy;

    @Spy
    private PositionsRepo positionsRepoSpy;

    @InjectMocks
    private TradeProcessorService tradeProcessorService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

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
        -   verify the calls to methods
        -   verify exceptional case - 4 branches
     */

    /* Integration Test

     */

//writeToJournalTableTest
    /* Mocked Test
        -   verify the calls to methods
        -   verify exceptional case
     */

    /* Integration Test
        -   null Trade
        -   invalid trade
        -   valid trade
            -   check the data and size of the table updated after write
        -   exceptional case
     */

//writeToPositionTableTest
    @ParameterizedTest
    @MethodSource("writeToPositionsTests")
    void writeToPositionsTable_MethodCallVerification_MockedTest(Trade trade) throws OptimisticLockingException {
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)) {
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(any())).thenReturn(positionsRepoSpy);
            doNothing().when(positionsRepoSpy).updatePositionsTable(any());
            tradeProcessorService.writeToPositionsTable(trade);
            verify(positionsRepoSpy, times(1)).updatePositionsTable(trade);
        }
    }

    static Stream<Arguments> writeToPositionsTests(){
        return Stream.of(
                Arguments.of(TestDataProvider.badTradeSupplier.get()),
                Arguments.of(TestDataProvider.goodTradeSupplier.get()),
                Arguments.of((Object) null)
        );
    }

    @Test
    void writeToPositionsTable_ExceptionCase_MockedTest() throws OptimisticLockingException {
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)) {
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(any())).thenReturn(positionsRepoSpy);
            doThrow(OptimisticLockingException.class).when(positionsRepoSpy).updatePositionsTable(any());
            assertThrows(OptimisticLockingException.class, () -> tradeProcessorService.writeToPositionsTable(trade));
            verify(positionsRepoSpy, times(1)).updatePositionsTable(trade);
        }
    }

    /* Integration Test TODO
        -   null Trade
        -   invalid trade
        -   valid trade
            -   check the data and size of the table updated after write
        -   exceptional case
     */

//updateJEPostedStatusInRawPayloadTest
    /* Mocked Test
        -   getPersistenceBean is called once
        -   updateJournalEntryStatusInRawPayloadsTable is called once
        -   assertThrows when updateJournalEntryStatusInRawPayloadsTable throws exception
     */

    /* Integration Test
        -   null trade
            -   methods getPersistenceBean and updateJournalEntryStatusInRawPayloadsTable called once
            -   exception occurs and thrown
        -   invalid trade
        -   valid trade
            -   methods getPersistenceBean and updateJournalEntryStatusInRawPayloadsTable called once
            -   check the posted status before and after the update
                    should reflect the changes
        -   assertThrows on exception occurring because of trade that causes exception
     */

//UpdatePositionPostedStatusInJETest
    /* Mocked Test
        -   Anything Happens
            -   updatePositionPostedStatusInJournalEntry is called Once
            -   getPersistenceBean is called once
        -   Assert Throws when updatePositionPostedStatusInJournalEntry throws UpdateException
     */

    /* Integration Test
        -   null trade
        -   invalid trade
        -   valid trade
            -   check before and after
                    should reflect the updated posted status
        -   ExceptionCase for PositionStatusUpdateInJournalEntry
     */
}
