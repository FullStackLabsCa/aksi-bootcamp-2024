package io.reactivestax.service;


import io.reactivestax.TestDataProvider;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivestax.utility.messaging.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChunkProcessorServiceTest {

    private static final String VALID = "Valid";
    private static final String INVALID = "Invalid";

    @Mock
    private RawPayloadRepo rawPayloadRepoMock;

    @Mock
    private MessageSender<TradeIdAndAccNum> messageSenderMock;

    @Spy
    private ChunkProcessorService chunkProcessorServiceSpy;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    //getInstanceTests
    @Test
    void getInstance_SingleThreadTest() {
        // Get two instances
        ChunkProcessorService instance1 = ChunkProcessorService.getInstance();
        ChunkProcessorService instance2 = ChunkProcessorService.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstance_MultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<ChunkProcessorService> getInstance = ChunkProcessorService::getInstance;

        // Get two instances
        ChunkProcessorService instance1 = executorService.submit(getInstance).get();
        ChunkProcessorService instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

//processChunkTest

//processPayloadTest
    @Test
    void processPayloadTest_ValidPayload(){
        String payload = TestDataProvider.validTradePayloadSupplier.get();
        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase("TDB_00000001", payload, VALID);
        verify(chunkProcessorServiceSpy, times(1)).sendForProcessing(new TradeIdAndAccNum("TDB_00000001", "TDB_CUST_2517563"));
    }

    @Test
    void processPayloadTest_InvalidPayload(){
        String payload = TestDataProvider.invalidPayloadLengthSupplier.get();
        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase("TDB_00000001", payload, INVALID);
        verify(chunkProcessorServiceSpy, times(0)).sendForProcessing(any());
    }

    @Test
    void processPayloadTest_NullPayload(){
        String payload = TestDataProvider.nullTradePayloadSupplier.get();
        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase(INVALID, payload, INVALID);
        verify(chunkProcessorServiceSpy, times(0)).sendForProcessing(any());
    }

    @Test
    void processPayloadTest_EmptyPayload(){

        doNothing().when(chunkProcessorServiceSpy).writePayloadToPayloadDatabase(any(), any(), any());

        String payload = TestDataProvider.emptyTradePayloadSupplier.get();
        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase(INVALID, payload, INVALID);
        verify(chunkProcessorServiceSpy, times(0)).sendForProcessing(any());
    }

    // processPayloadTest_VerifyDBInsertionOfPayload # TODO Integration Test
    // processPayloadTest_VerifyQueueInsertion # TODO Integration Test

//checkPayloadValidityTests
    @Test
    void checkPayloadValidityTest_ValidPayload(){
        String payload = TestDataProvider.validTradePayloadSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        assertEquals(VALID, result);
    }

    @Test
    void checkPayloadValidityTest_InvalidPayload(){
        String payload = TestDataProvider.invalidPayloadLengthSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        assertEquals(INVALID, result);
    }

    @Test
    void checkPayloadValidityTest_EmptyStringPayload(){
        String payload = TestDataProvider.emptyTradePayloadSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        assertEquals(INVALID, result);
    }

    @Test
    void checkPayloadValidityTest_NullPayload(){
        String payload = TestDataProvider.nullTradePayloadSupplier.get();
        String result = ChunkProcessorService.getInstance().checkPayloadValidity(payload);
        assertEquals(INVALID, result);
    }

//getIdentifierFromPayloadTest
    @Test
    void getIdentifierFromPayloadTest_ValidAccountNumber_ValidTradeId(){
        String payload = TestDataProvider.validTradePayloadSupplier.get();
        TradeIdAndAccNum identifierFromPayload = ChunkProcessorService.getInstance().getIdentifierFromPayload(payload);

        assertEquals("TDB_00000001", identifierFromPayload.tradeID());
        assertEquals("TDB_CUST_2517563", identifierFromPayload.accountNumber());
    }

    @Test
    void getIdentifierFromPayloadTest_InvalidAccountNumber_ValidTradeId(){
        String payload = TestDataProvider.invalidPayloadAccountNumberSupplier.get();
        TradeIdAndAccNum identifierFromPayload = ChunkProcessorService.getInstance().getIdentifierFromPayload(payload);

        assertEquals("TDB_00000001", identifierFromPayload.tradeID());
        assertEquals(INVALID, identifierFromPayload.accountNumber());
    }

    @Test
    void getIdentifierFromPayloadTest_InvalidAccountNumber_InvalidTradeId(){
        String payload = TestDataProvider.nullTradePayloadSupplier.get();
        TradeIdAndAccNum identifierFromPayload = ChunkProcessorService.getInstance().getIdentifierFromPayload(payload);

        assertEquals(INVALID, identifierFromPayload.tradeID());
        assertEquals(INVALID, identifierFromPayload.accountNumber());
    }

//writePayloadToPayloadDatabaseTest
    @Test
    void writePayloadToPayloadDBTest(){
        String tradeId = TestDataProvider.validTradeIdSupplier.get();
        String payload = TestDataProvider.validTradePayloadSupplier.get();
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            beanFactoryMockedStatic.when(BeanFactory::getRawPayloadRepo).thenReturn(rawPayloadRepoMock);
            ChunkProcessorService.getInstance().writePayloadToPayloadDatabase(tradeId, payload, VALID);

            beanFactoryMockedStatic.verify(BeanFactory::getRawPayloadRepo, times(1));
            verify(rawPayloadRepoMock, times(1)).writeToRawPayloadTable(any(), any(), any());
        }
    }

//sendForProcessingTest
    @Test
    void sendForProcessingTest(){
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            beanFactoryMockedStatic.when(BeanFactory::getMessageSender).thenReturn(messageSenderMock);
            ChunkProcessorService.getInstance().sendForProcessing(TestDataProvider.validTradeIdentifierSupplier.get());

            beanFactoryMockedStatic.verify(BeanFactory::getMessageSender, times(1));
            verify(messageSenderMock, times(1)).sendMessage(any());
        }
    }
}
