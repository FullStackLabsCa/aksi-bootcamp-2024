package io.reactivestax.service;


import io.reactivestax.TestDataProvider;
import io.reactivestax.factory.BeanFactory;
import io.reactivestax.model.Trade;
import io.reactivestax.repo.JournalEntryRepo;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.database.TransactionUtil;
import io.reactivestax.utility.exceptions.*;
import io.reactivestax.utility.messaging.MessageProvider;
import io.reactivestax.utility.messaging.MessageReceiver;
import io.reactivestax.utility.messaging.MessageRetry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.util.Optional;
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

    private static final String CREATE_TABLE_SECURITIES_REFERENCE = """
            create table SecuritiesReferenceV2 (
                    cusip varchar(15) not null unique,
                    security_id int not null unique
            );""";
    private static final String POPULATE_TABLE_SECURITIES_REFERENCE = "insert into SecuritiesReferenceV2 (cusip, security_id) values ('TSLA', 157001093);";
    private static final String DELETE_FROM_SECURITIES_REFERENCE_V_2 = "delete from SecuritiesReferenceV2";

    @Mock
    private JournalEntryRepo journalEntryRepoSpy;

    @Mock
    private PositionsRepo positionsRepoSpy;

    @Mock
    private RawPayloadRepo rawPayloadRepoSpy;

    @Mock
    private MessageReceiver<String> messageReceiverSpy;

    @Mock
    private MessageProvider messageProviderSpy;

    @Mock
    private MessageRetry<Trade> messageRetryerSpy;

    @Mock
    private TransactionUtil transactionUtilSpy;

    @InjectMocks
    @Spy
    private TradeProcessorService tradeProcessorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInstanceSingleThreadTest() {
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
    @Test
    void runTradeProcessorTest_ValidCusipTrade() throws WriteToJournalEntryFailed, OptimisticLockingOccurrence, UpdateJournalEntryStatusInRawPayloadFailed, UpdatePositionStatusInJournalEntryFailed {
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            String tradeId = TestDataProvider.tradeIdSupplier.get();
            String payload = TestDataProvider.validTradePayloadSupplier.get();
            Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();

            //Setup
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(TransactionUtil.class)).thenReturn(transactionUtilSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(RawPayloadRepo.class)).thenReturn(rawPayloadRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(JournalEntryRepo.class)).thenReturn(journalEntryRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(PositionsRepo.class)).thenReturn(positionsRepoSpy);
            beanFactoryMockedStatic.when(BeanFactory::getMessageRetryer).thenReturn(messageRetryerSpy);

            when(tradeProcessorService.getTradeID(any()))
                    .thenReturn(Optional.of(tradeId))
                    .thenReturn(Optional.empty());
            doReturn(Optional.of(payload)).when(tradeProcessorService).readPayloadFromRawPayloadDB(any());
            doReturn(trade).when(tradeProcessorService).validatePayloadAndCreateTrade(any());
            doReturn("Valid").when(tradeProcessorService).validateBusinessLogic(any());

            doNothing().when(rawPayloadRepoSpy).updateSecurityLookupStatusInRawPayloadsTable(any(), any());
            doNothing().when(rawPayloadRepoSpy).updateJournalEntryStatusInRawPayloadsTable(any());
            doNothing().when(journalEntryRepoSpy).writeTradeToJournalEntryTable(any());
            doNothing().when(journalEntryRepoSpy).updatePositionPostedStatusInJournalEntry(any());
            doNothing().when(positionsRepoSpy).updatePositionsTable(any());

            //Action
            tradeProcessorService.runTradeProcessor(messageProviderSpy);

            //Assert
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
            verify(messageReceiverSpy, times(1)).receiveMessage(any());
            verify(tradeProcessorService, times(2)).getTradeID(any());
            verify(tradeProcessorService, times(1)).readPayloadFromRawPayloadDB(tradeId);
            verify(tradeProcessorService, times(1)).validatePayloadAndCreateTrade(payload);
            //processTradeMethodVerification
            verify(tradeProcessorService, times(1)).validateBusinessLogic(trade);
            ////updateTradeSecurityLookUpInPayloadTable
            verify(rawPayloadRepoSpy, times(1)).updateSecurityLookupStatusInRawPayloadsTable(trade, "Valid");
            ////updateJournalEntryAndPositions
            verify(transactionUtilSpy, times(1)).startTransaction();
            verify(journalEntryRepoSpy,times(1)).writeTradeToJournalEntryTable(trade);
            verify(rawPayloadRepoSpy, times(1)).updateJournalEntryStatusInRawPayloadsTable(trade);
            verify(positionsRepoSpy, times(1)).updatePositionsTable(trade);
            verify(journalEntryRepoSpy, times(1)).updatePositionPostedStatusInJournalEntry(trade);
            verify(transactionUtilSpy, times(1)).commitTransaction();
            verify(transactionUtilSpy, times(0)).rollbackTransaction();
            verify(messageRetryerSpy, times(0)).retryMessage(any());
        }
    }

    @Test
    void runTradeProcessorTest_InvalidCusipTrade() throws WriteToJournalEntryFailed, OptimisticLockingOccurrence, UpdateJournalEntryStatusInRawPayloadFailed, UpdatePositionStatusInJournalEntryFailed {
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            String tradeId = TestDataProvider.tradeIdSupplier.get();
            String payload = TestDataProvider.validTradePayloadSupplier.get();
            Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();

            //Setup
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(TransactionUtil.class)).thenReturn(transactionUtilSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(RawPayloadRepo.class)).thenReturn(rawPayloadRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(JournalEntryRepo.class)).thenReturn(journalEntryRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(PositionsRepo.class)).thenReturn(positionsRepoSpy);
            beanFactoryMockedStatic.when(BeanFactory::getMessageRetryer).thenReturn(messageRetryerSpy);

            when(tradeProcessorService.getTradeID(any()))
                    .thenReturn(Optional.of(tradeId))
                    .thenReturn(Optional.empty());
            doReturn(Optional.of(payload)).when(tradeProcessorService).readPayloadFromRawPayloadDB(any());
            doReturn(trade).when(tradeProcessorService).validatePayloadAndCreateTrade(any());
            doReturn("Invalid").when(tradeProcessorService).validateBusinessLogic(any());

            //Action
            tradeProcessorService.runTradeProcessor(messageProviderSpy);

            //Assert
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
            verify(messageReceiverSpy, times(1)).receiveMessage(any());
            verify(tradeProcessorService, times(2)).getTradeID(any());
            verify(tradeProcessorService, times(1)).readPayloadFromRawPayloadDB(tradeId);
            verify(tradeProcessorService, times(1)).validatePayloadAndCreateTrade(payload);
            //processTradeMethodVerification
            verify(tradeProcessorService, times(1)).validateBusinessLogic(trade);
            ////updateTradeSecurityLookUpInPayloadTable
            verify(rawPayloadRepoSpy, times(1)).updateSecurityLookupStatusInRawPayloadsTable(trade, "Invalid");
            ////updateJournalEntryAndPositions
            verify(transactionUtilSpy, times(0)).startTransaction();
            verify(journalEntryRepoSpy,times(0)).writeTradeToJournalEntryTable(trade);
            verify(rawPayloadRepoSpy, times(0)).updateJournalEntryStatusInRawPayloadsTable(trade);
            verify(positionsRepoSpy, times(0)).updatePositionsTable(trade);
            verify(journalEntryRepoSpy, times(0)).updatePositionPostedStatusInJournalEntry(trade);
            verify(transactionUtilSpy, times(0)).commitTransaction();
            verify(transactionUtilSpy, times(0)).rollbackTransaction();
            verify(messageRetryerSpy, times(0)).retryMessage(any());
        }
    }

    @Test
    void runTradeProcessorTest_WriteJournalEntryFailed() throws WriteToJournalEntryFailed, OptimisticLockingOccurrence, UpdateJournalEntryStatusInRawPayloadFailed, UpdatePositionStatusInJournalEntryFailed {
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            String tradeId = TestDataProvider.tradeIdSupplier.get();
            String payload = TestDataProvider.validTradePayloadSupplier.get();
            Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();

            //Setup
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(TransactionUtil.class)).thenReturn(transactionUtilSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(RawPayloadRepo.class)).thenReturn(rawPayloadRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(JournalEntryRepo.class)).thenReturn(journalEntryRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(PositionsRepo.class)).thenReturn(positionsRepoSpy);
            beanFactoryMockedStatic.when(BeanFactory::getMessageRetryer).thenReturn(messageRetryerSpy);

            when(tradeProcessorService.getTradeID(any()))
                    .thenReturn(Optional.of(tradeId))
                    .thenReturn(Optional.empty());
            doReturn(Optional.of(payload)).when(tradeProcessorService).readPayloadFromRawPayloadDB(any());
            doReturn(trade).when(tradeProcessorService).validatePayloadAndCreateTrade(any());
            doReturn("Valid").when(tradeProcessorService).validateBusinessLogic(any());

            doNothing().when(rawPayloadRepoSpy).updateSecurityLookupStatusInRawPayloadsTable(any(), any());
            doNothing().when(messageRetryerSpy).retryMessage(any());
            doThrow(WriteToJournalEntryFailed.class).when(journalEntryRepoSpy).writeTradeToJournalEntryTable(any());

            //Action
            tradeProcessorService.runTradeProcessor(messageProviderSpy);

            //Assert
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
            verify(messageReceiverSpy, times(1)).receiveMessage(any());
            verify(tradeProcessorService, times(2)).getTradeID(any());
            verify(tradeProcessorService, times(1)).readPayloadFromRawPayloadDB(tradeId);
            verify(tradeProcessorService, times(1)).validatePayloadAndCreateTrade(payload);
            //processTradeMethodVerification
            verify(tradeProcessorService, times(1)).validateBusinessLogic(trade);
            ////updateTradeSecurityLookUpInPayloadTable
            verify(rawPayloadRepoSpy, times(1)).updateSecurityLookupStatusInRawPayloadsTable(trade, "Valid");
            ////updateJournalEntryAndPositions
            verify(transactionUtilSpy, times(1)).startTransaction();
            verify(journalEntryRepoSpy,times(1)).writeTradeToJournalEntryTable(trade);
            verify(rawPayloadRepoSpy, times(0)).updateJournalEntryStatusInRawPayloadsTable(trade);
            verify(positionsRepoSpy, times(0)).updatePositionsTable(trade);
            verify(journalEntryRepoSpy, times(0)).updatePositionPostedStatusInJournalEntry(trade);
            verify(transactionUtilSpy, times(0)).commitTransaction();
            verify(transactionUtilSpy, times(1)).rollbackTransaction();
            verify(messageRetryerSpy, times(1)).retryMessage(trade);

        }
    }

    @Test
    void runTradeProcessorTest_UpdateJournalEntryStatusInRawPayloadFailed() throws WriteToJournalEntryFailed, OptimisticLockingOccurrence, UpdateJournalEntryStatusInRawPayloadFailed, UpdatePositionStatusInJournalEntryFailed {
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            String tradeId = TestDataProvider.tradeIdSupplier.get();
            String payload = TestDataProvider.validTradePayloadSupplier.get();
            Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();

            //Setup
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(TransactionUtil.class)).thenReturn(transactionUtilSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(RawPayloadRepo.class)).thenReturn(rawPayloadRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(JournalEntryRepo.class)).thenReturn(journalEntryRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(PositionsRepo.class)).thenReturn(positionsRepoSpy);
            beanFactoryMockedStatic.when(BeanFactory::getMessageRetryer).thenReturn(messageRetryerSpy);

            when(tradeProcessorService.getTradeID(any()))
                    .thenReturn(Optional.of(tradeId))
                    .thenReturn(Optional.empty());
            doReturn(Optional.of(payload)).when(tradeProcessorService).readPayloadFromRawPayloadDB(any());
            doReturn(trade).when(tradeProcessorService).validatePayloadAndCreateTrade(any());
            doReturn("Valid").when(tradeProcessorService).validateBusinessLogic(any());

            doNothing().when(rawPayloadRepoSpy).updateSecurityLookupStatusInRawPayloadsTable(any(), any());
            doNothing().when(messageRetryerSpy).retryMessage(any());
            doThrow(UpdateJournalEntryStatusInRawPayloadFailed.class).when(rawPayloadRepoSpy).updateJournalEntryStatusInRawPayloadsTable(any());

            //Action
            tradeProcessorService.runTradeProcessor(messageProviderSpy);

            //Assert
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
            verify(messageReceiverSpy, times(1)).receiveMessage(any());
            verify(tradeProcessorService, times(2)).getTradeID(any());
            verify(tradeProcessorService, times(1)).readPayloadFromRawPayloadDB(tradeId);
            verify(tradeProcessorService, times(1)).validatePayloadAndCreateTrade(payload);
            //processTradeMethodVerification
            verify(tradeProcessorService, times(1)).validateBusinessLogic(trade);
            ////updateTradeSecurityLookUpInPayloadTable
            verify(rawPayloadRepoSpy, times(1)).updateSecurityLookupStatusInRawPayloadsTable(trade, "Valid");
            ////updateJournalEntryAndPositions
            verify(transactionUtilSpy, times(1)).startTransaction();
            verify(journalEntryRepoSpy,times(1)).writeTradeToJournalEntryTable(trade);
            verify(rawPayloadRepoSpy, times(1)).updateJournalEntryStatusInRawPayloadsTable(trade);
            verify(positionsRepoSpy, times(0)).updatePositionsTable(trade);
            verify(journalEntryRepoSpy, times(0)).updatePositionPostedStatusInJournalEntry(trade);
            verify(transactionUtilSpy, times(0)).commitTransaction();
            verify(transactionUtilSpy, times(1)).rollbackTransaction();
            verify(messageRetryerSpy, times(1)).retryMessage(trade);

        }
    }

    @Test
    void runTradeProcessorTest_OptimisticLockingException() throws WriteToJournalEntryFailed, OptimisticLockingOccurrence, UpdateJournalEntryStatusInRawPayloadFailed, UpdatePositionStatusInJournalEntryFailed {
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            String tradeId = TestDataProvider.tradeIdSupplier.get();
            String payload = TestDataProvider.validTradePayloadSupplier.get();
            Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();

            //Setup
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(TransactionUtil.class)).thenReturn(transactionUtilSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(RawPayloadRepo.class)).thenReturn(rawPayloadRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(JournalEntryRepo.class)).thenReturn(journalEntryRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(PositionsRepo.class)).thenReturn(positionsRepoSpy);
            beanFactoryMockedStatic.when(BeanFactory::getMessageRetryer).thenReturn(messageRetryerSpy);

            when(tradeProcessorService.getTradeID(any()))
                    .thenReturn(Optional.of(tradeId))
                    .thenReturn(Optional.empty());
            doReturn(Optional.of(payload)).when(tradeProcessorService).readPayloadFromRawPayloadDB(any());
            doReturn(trade).when(tradeProcessorService).validatePayloadAndCreateTrade(any());
            doReturn("Valid").when(tradeProcessorService).validateBusinessLogic(any());

            doNothing().when(rawPayloadRepoSpy).updateSecurityLookupStatusInRawPayloadsTable(any(), any());
            doNothing().when(rawPayloadRepoSpy).updateJournalEntryStatusInRawPayloadsTable(any());
            doNothing().when(messageRetryerSpy).retryMessage(any());
            doNothing().when(journalEntryRepoSpy).writeTradeToJournalEntryTable(any());
            doThrow(OptimisticLockingOccurrence.class).when(positionsRepoSpy).updatePositionsTable(any());

            //Action
            tradeProcessorService.runTradeProcessor(messageProviderSpy);

            //Assert
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
            verify(messageReceiverSpy, times(1)).receiveMessage(any());
            verify(tradeProcessorService, times(2)).getTradeID(any());
            verify(tradeProcessorService, times(1)).readPayloadFromRawPayloadDB(tradeId);
            verify(tradeProcessorService, times(1)).validatePayloadAndCreateTrade(payload);
            //processTradeMethodVerification
            verify(tradeProcessorService, times(1)).validateBusinessLogic(trade);
            ////updateTradeSecurityLookUpInPayloadTable
            verify(rawPayloadRepoSpy, times(1)).updateSecurityLookupStatusInRawPayloadsTable(trade, "Valid");
            ////updateJournalEntryAndPositions
            verify(transactionUtilSpy, times(1)).startTransaction();
            verify(journalEntryRepoSpy,times(1)).writeTradeToJournalEntryTable(trade);
            verify(rawPayloadRepoSpy, times(1)).updateJournalEntryStatusInRawPayloadsTable(trade);
            verify(positionsRepoSpy, times(1)).updatePositionsTable(trade);
            verify(journalEntryRepoSpy, times(0)).updatePositionPostedStatusInJournalEntry(trade);
            verify(transactionUtilSpy, times(0)).commitTransaction();
            verify(transactionUtilSpy, times(1)).rollbackTransaction();
            verify(messageRetryerSpy, times(1)).retryMessage(trade);
        }
    }

    @Test
    void runTradeProcessorTest_UpdatePositionStatusInJournalEntryFailed() throws WriteToJournalEntryFailed, OptimisticLockingOccurrence, UpdateJournalEntryStatusInRawPayloadFailed, UpdatePositionStatusInJournalEntryFailed {
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){

            String tradeId = TestDataProvider.tradeIdSupplier.get();
            String payload = TestDataProvider.validTradePayloadSupplier.get();
            Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();

            //Setup
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(TransactionUtil.class)).thenReturn(transactionUtilSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(RawPayloadRepo.class)).thenReturn(rawPayloadRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(JournalEntryRepo.class)).thenReturn(journalEntryRepoSpy);
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(PositionsRepo.class)).thenReturn(positionsRepoSpy);
            beanFactoryMockedStatic.when(BeanFactory::getMessageRetryer).thenReturn(messageRetryerSpy);

            when(tradeProcessorService.getTradeID(any()))
                    .thenReturn(Optional.of(tradeId))
                    .thenReturn(Optional.empty());
            doReturn(Optional.of(payload)).when(tradeProcessorService).readPayloadFromRawPayloadDB(any());
            doReturn(trade).when(tradeProcessorService).validatePayloadAndCreateTrade(any());
            doReturn("Valid").when(tradeProcessorService).validateBusinessLogic(any());

            doNothing().when(rawPayloadRepoSpy).updateSecurityLookupStatusInRawPayloadsTable(any(), any());
            doNothing().when(rawPayloadRepoSpy).updateJournalEntryStatusInRawPayloadsTable(any());
            doNothing().when(messageRetryerSpy).retryMessage(any());
            doNothing().when(journalEntryRepoSpy).writeTradeToJournalEntryTable(any());
            doThrow(UpdatePositionStatusInJournalEntryFailed.class).when(journalEntryRepoSpy).updatePositionPostedStatusInJournalEntry(any());
            doNothing().when(positionsRepoSpy).updatePositionsTable(any());

            //Action
            tradeProcessorService.runTradeProcessor(messageProviderSpy);

            //Assert
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
            verify(messageReceiverSpy, times(1)).receiveMessage(any());
            verify(tradeProcessorService, times(2)).getTradeID(any());
            verify(tradeProcessorService, times(1)).readPayloadFromRawPayloadDB(tradeId);
            verify(tradeProcessorService, times(1)).validatePayloadAndCreateTrade(payload);
            //processTradeMethodVerification
            verify(tradeProcessorService, times(1)).validateBusinessLogic(trade);
            ////updateTradeSecurityLookUpInPayloadTable
            verify(rawPayloadRepoSpy, times(1)).updateSecurityLookupStatusInRawPayloadsTable(trade, "Valid");
            ////updateJournalEntryAndPositions
            verify(transactionUtilSpy, times(1)).startTransaction();
            verify(journalEntryRepoSpy,times(1)).writeTradeToJournalEntryTable(trade);
            verify(rawPayloadRepoSpy, times(1)).updateJournalEntryStatusInRawPayloadsTable(trade);
            verify(positionsRepoSpy, times(1)).updatePositionsTable(trade);
            verify(journalEntryRepoSpy, times(1)).updatePositionPostedStatusInJournalEntry(trade);
            verify(transactionUtilSpy, times(0)).commitTransaction();
            verify(transactionUtilSpy, times(1)).rollbackTransaction();
            verify(messageRetryerSpy, times(1)).retryMessage(trade);
        }
    }

//getTradeIdTest
    @ParameterizedTest
    @MethodSource("getTradeIdTests_MessageProviders")
    void getTradeIdTest_NullProvider(MessageProvider messageProvider){
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            doReturn(null).when(messageReceiverSpy).receiveMessage(any());
            tradeProcessorService.getTradeID(messageProvider);
            verify(messageReceiverSpy, times(1)).receiveMessage(messageProvider);
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
        }
    }

    static Stream<Arguments> getTradeIdTests_MessageProviders(){
        return Stream.of(
                Arguments.of((Object) null)
        );
    }

    @Test
    void getTradeIdTest_MessageProvider(){
        MessageProvider messageProviderFromBeanFactory = BeanFactory.getMessageProvider(1);
        try(MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)){
            beanFactoryMockedStatic.when(BeanFactory::getMessageReceiver).thenReturn(messageReceiverSpy);
            doReturn(null).when(messageReceiverSpy).receiveMessage(any());
            tradeProcessorService.getTradeID(messageProviderFromBeanFactory);
            verify(messageReceiverSpy, times(1)).receiveMessage(messageProviderFromBeanFactory);
            beanFactoryMockedStatic.verify(BeanFactory::getMessageReceiver, times(1));
        }
    }

//readPayloadFromRawDatabaseTest
    @ParameterizedTest
    @MethodSource("readPayloadFromRawPayloadDBTests")
    void readPayloadFromRawPayloadDBTest_ReadBeforeInsertion(String tradeId) {
        try (MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)) {
            //Setup
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(any())).thenReturn(rawPayloadRepoSpy);
            doReturn(Optional.empty()).when(rawPayloadRepoSpy).readPayloadFromRawPayloadsTable(any());
            //Action
            Optional<String> payloadFromDB = tradeProcessorService.readPayloadFromRawPayloadDB(tradeId);
            //Assert
            assertEquals(Optional.empty(), payloadFromDB);
            verify(rawPayloadRepoSpy, times(1)).readPayloadFromRawPayloadsTable(tradeId);
        }
    }

    static Stream<Arguments> readPayloadFromRawPayloadDBTests() {
        return Stream.of(
                Arguments.of(TestDataProvider.tradeIdSupplier.get()),
                Arguments.of(""),
                Arguments.of((Object) null)
        );
    }

//validatePayloadAndCreateChunkTest
    @ParameterizedTest
    @MethodSource("validatePayloadAndCreateTradeFailingTests")
    void validatePayloadAndCreateTradeTest_NullPayload(String payload){
        assertThrows(TradeCreationFailedException.class, () -> TradeProcessorService.getInstance().validatePayloadAndCreateTrade(payload));
    }

    static Stream<Arguments> validatePayloadAndCreateTradeFailingTests(){
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(""),
                Arguments.of(TestDataProvider.invalidTradePayloadSupplier.get())
        );
    }

    @Test
    void validatePayloadAndCreateChunkTest_ValidPayload(){
        Trade expectedTrade = TestDataProvider.validTradeForPayloadSupplier.get();
        String payload = TestDataProvider.validTradePayloadSupplier.get();

        assertEquals(expectedTrade, TradeProcessorService.getInstance().validatePayloadAndCreateTrade(payload));
    }

//convertStringToSQLDateTest
    /* Mocked Test

     */

    /* Integration Test

     */


//validateBusinessLogicTest
    @ParameterizedTest
    @MethodSource("validateBusinessLogicTests")
    void validateBusinessLogic_MethodCallVerification_MockedTest(Trade trade, String validity){
        ApplicationPropertyUtils.readPropertiesFile("src/test/resources/test.application.properties");
        createSecurityRefTableAndPopulateIt();
        assertEquals(validity, TradeProcessorService.getInstance().validateBusinessLogic(trade));
        dropSecuritiesRefTable();
        ApplicationPropertyUtils.resetProperties();
    }

    static Stream<Arguments> validateBusinessLogicTests() {
        return Stream.of(
                Arguments.of(TestDataProvider.invalidCusipTradeSupplier.get(), "Invalid"),
                Arguments.of(TestDataProvider.goodTradeSupplier.get(), "Valid"),
                Arguments.of( null, "Unable to Check CUSIP.")
        );
    }

    private void createSecurityRefTableAndPopulateIt(){
        try (PreparedStatement createSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(CREATE_TABLE_SECURITIES_REFERENCE)) {

            JDBCUtils.getInstance().startTransaction();
            createSecRefTableStmt.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();

        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }

        try (PreparedStatement populateSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(POPULATE_TABLE_SECURITIES_REFERENCE)) {

            JDBCUtils.getInstance().startTransaction();
            populateSecRefTableStmt.executeUpdate();
            JDBCUtils.getInstance().commitTransaction();

        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }
    }

    private void dropSecuritiesRefTable(){
        try (PreparedStatement dropSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(DELETE_FROM_SECURITIES_REFERENCE_V_2)) {
            JDBCUtils.getInstance().startTransaction();

            dropSecRefTableStmt.executeUpdate();

            JDBCUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }
    }

//writeToJournalTableTest
    @ParameterizedTest
    @MethodSource("writeToJournalTableTests")
    void writeToJournalTable_MethodCallVerification_MockedTest(Trade trade) throws WriteToJournalEntryFailed {
        try (MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)) {
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(any())).thenReturn(journalEntryRepoSpy);
            doNothing().when(journalEntryRepoSpy).writeTradeToJournalEntryTable(any());
            tradeProcessorService.writeToJournalTable(trade);
            verify(journalEntryRepoSpy, times(1)).writeTradeToJournalEntryTable(trade);
        }
    }

    static Stream<Arguments> writeToJournalTableTests() {
        return Stream.of(
                Arguments.of(TestDataProvider.badTradeSupplier.get()),
                Arguments.of(TestDataProvider.goodTradeSupplier.get()),
                Arguments.of((Object) null)
        );
    }

    @Test
    void writeToJournalTable_ExceptionCase_MockedTest() throws WriteToJournalEntryFailed {
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        try (MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)) {
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(any())).thenReturn(journalEntryRepoSpy);
            doThrow(WriteToJournalEntryFailed.class).when(journalEntryRepoSpy).writeTradeToJournalEntryTable(any());
            assertThrows(WriteToJournalEntryFailed.class, () -> tradeProcessorService.writeToJournalTable(trade));
            verify(journalEntryRepoSpy, times(1)).writeTradeToJournalEntryTable(trade);
        }
    }

    /* Integration Test TODO
        -   null Trade
        -   invalid trade
        -   valid trade
            -   check the data and size of the table updated after write
        -   exceptional case
     */

//writeToPositionTableTest
    @ParameterizedTest
    @MethodSource("writeToPositionsTests")
    void writeToPositionsTable_MethodCallVerification_MockedTest(Trade trade) throws OptimisticLockingOccurrence {
        try (MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)) {
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(any())).thenReturn(positionsRepoSpy);
            doNothing().when(positionsRepoSpy).updatePositionsTable(any());
            tradeProcessorService.writeToPositionsTable(trade);
            verify(positionsRepoSpy, times(1)).updatePositionsTable(trade);
        }
    }

    static Stream<Arguments> writeToPositionsTests() {
        return Stream.of(
                Arguments.of(TestDataProvider.badTradeSupplier.get()),
                Arguments.of(TestDataProvider.goodTradeSupplier.get()),
                Arguments.of((Object) null)
        );
    }

    @Test
    void writeToPositionsTable_ExceptionCase_MockedTest() throws OptimisticLockingOccurrence {
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        try (MockedStatic<BeanFactory> beanFactoryMockedStatic = Mockito.mockStatic(BeanFactory.class)) {
            beanFactoryMockedStatic.when(() -> BeanFactory.getPersistenceBean(any())).thenReturn(positionsRepoSpy);
            doThrow(OptimisticLockingOccurrence.class).when(positionsRepoSpy).updatePositionsTable(any());
            assertThrows(OptimisticLockingOccurrence.class, () -> tradeProcessorService.writeToPositionsTable(trade));
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

}
