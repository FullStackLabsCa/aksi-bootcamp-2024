package io.reactivestax.service;


import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.RawPayload;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.service.interfaces.TradeIdAndAccNum;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import io.reactivestax.utility.exceptions.FilepathProcessingException;
import io.reactivestax.utility.messaging.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @Test
    void processChunkTest_InvalidFilePath(){
        assertThrows(FilepathProcessingException.class, () -> chunkProcessorServiceSpy.processChunk(TestDataProvider.invalidFilePathSupplier.get()));
    }

    @Test
    void processChunkTest_EmptyFilePath(){
        assertThrows(FilepathProcessingException.class, () -> chunkProcessorServiceSpy.processChunk(""));
    }

    @Test
    void processChunkTest_NullFilePath(){
        assertThrows(FilepathProcessingException.class, () -> chunkProcessorServiceSpy.processChunk(null));
    }

    @ParameterizedTest
    @MethodSource("tradesChunkProcessTestCases")
    void processChunk_ValidTradesFile(String filePath, int numOfTradesInFile){

        doNothing().when(chunkProcessorServiceSpy).processPayload(any());

        chunkProcessorServiceSpy.processChunk(filePath);
        verify(chunkProcessorServiceSpy, times(numOfTradesInFile + 1)).processPayload(any()); // +1 for the header
    }

    static Stream<Arguments> tradesChunkProcessTestCases(){
        return Stream.of(
                Arguments.of( TestDataProvider.trades_0_FileAbsPathSupplier.get(), 0),
                Arguments.of(TestDataProvider.trades_10000_FileAbsPathSupplier.get(), 10000),
                Arguments.of(TestDataProvider.trades_800_FileAbsPathSupplier.get(), 800),
                Arguments.of(TestDataProvider.trades_999_FileAbsPathSupplier.get(), 999),
                Arguments.of(TestDataProvider.trades_1111_FileAbsPathSupplier.get(), 1111)
        );
    }

    // processChunkTest_ValidateDataInsertionInDB # TODO Integration Test
    // processChunkTest_ValidateDataInsertionInRabbitMQ # TODO Integration Test

//processPayloadTest
    @Test
    void processPayloadTest_ValidPayload(){
        String payload = TestDataProvider.validTradePayloadSupplier.get();
        RawPayload rawPayload = TestDataProvider.validRawPayloadSupplier.get();
        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).getRawPayloadFromStringPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase(rawPayload);
        verify(chunkProcessorServiceSpy, times(1)).sendForProcessing(new TradeIdAndAccNum("TDB_00000001", "TDB_CUST_2517563"));
    }

    @Test
    void processPayloadTest_InvalidPayload(){
        String payload = TestDataProvider.invalidPayloadLengthSupplier.get();
        RawPayload rawPayload = TestDataProvider.invalidRawPayloadSupplier.get();
        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).getRawPayloadFromStringPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase(rawPayload);
        verify(chunkProcessorServiceSpy, times(0)).sendForProcessing(any());
    }

    @Test
    void processPayloadTest_NullPayload(){
        String payload = TestDataProvider.nullTradePayloadSupplier.get();
        RawPayload rawPayload = TestDataProvider.nullRawPayloadSupplier.get();
        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).getRawPayloadFromStringPayload(payload);
        verify(chunkProcessorServiceSpy, times(0)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase(rawPayload);
        verify(chunkProcessorServiceSpy, times(0)).sendForProcessing(any());
    }

    @Test
    void processPayloadTest_EmptyPayload(){

        doNothing().when(chunkProcessorServiceSpy).writePayloadToPayloadDatabase(any());

        String payload = TestDataProvider.emptyTradePayloadSupplier.get();
        RawPayload rawPayload = TestDataProvider.nullRawPayloadSupplier.get();

        chunkProcessorServiceSpy.processPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).getRawPayloadFromStringPayload(payload);
        verify(chunkProcessorServiceSpy, times(0)).checkPayloadValidity(payload);
        verify(chunkProcessorServiceSpy, times(1)).getIdentifierFromPayload(payload);
        verify(chunkProcessorServiceSpy, times(1)).writePayloadToPayloadDatabase(rawPayload);
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

        assertEquals(TestDataProvider.validTradeIdSupplier.get(), identifierFromPayload.tradeID());
        assertEquals(INVALID, identifierFromPayload.accountNumber());
    }

    @Test
    void getIdentifierFromPayloadTest_InvalidAccountNumber_InvalidTradeId(){
        String payload = TestDataProvider.nullTradePayloadSupplier.get();
        TradeIdAndAccNum identifierFromPayload = ChunkProcessorService.getInstance().getIdentifierFromPayload(payload);

        assertEquals(INVALID, identifierFromPayload.tradeID());
        assertEquals(INVALID, identifierFromPayload.accountNumber());
    }

//getRawPayloadFromStringPayload
    @Test
    void getRawPayloadFromStringPayloadTest_NullPayload(){
        RawPayload expectedPayload = TestDataProvider.nullRawPayloadSupplier.get();
        assertEquals(expectedPayload, ChunkProcessorService.getInstance().getRawPayloadFromStringPayload(null));
    }

    @Test
    void getRawPayloadFromStringPayloadTest_EmptyPayload(){
        RawPayload expectedPayload = TestDataProvider.nullRawPayloadSupplier.get();
        assertEquals(expectedPayload, ChunkProcessorService.getInstance().getRawPayloadFromStringPayload(""));
    }

    @Test
    void getRawPayloadFromStringPayloadTest_ValidPayload(){
        RawPayload expectedPayload = TestDataProvider.validRawPayloadSupplier.get();
        assertEquals(expectedPayload, ChunkProcessorService.getInstance().getRawPayloadFromStringPayload(TestDataProvider.validTradePayloadSupplier.get()));
    }

//writePayloadToPayloadDatabaseTest
    @Test
    void writePayloadToPayloadDBTest(){
        RawPayload rawPayload = TestDataProvider.validRawPayloadSupplier.get();
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            beanFactoryMockedStatic.when(BeanFactory::getRawPayloadRepo).thenReturn(rawPayloadRepoMock);
            ChunkProcessorService.getInstance().writePayloadToPayloadDatabase(rawPayload);

            beanFactoryMockedStatic.verify(BeanFactory::getRawPayloadRepo, times(1));
            verify(rawPayloadRepoMock, times(1)).writeToRawPayloadTable(any());
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
