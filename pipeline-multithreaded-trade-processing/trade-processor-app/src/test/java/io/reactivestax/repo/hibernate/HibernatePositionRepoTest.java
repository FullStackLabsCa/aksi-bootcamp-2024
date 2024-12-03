package io.reactivestax.repo.hibernate;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingOccurrence;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;

class HibernatePositionRepoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String CREATE_TABLE_SECURITIES_REFERENCE = """
            create table if not exists SecuritiesReferenceV2 (
                    cusip varchar(15) not null unique,
                    security_id int not null unique
            );""";
    private static final String POPULATE_TABLE_SECURITIES_REFERENCE = "insert into SecuritiesReferenceV2 (cusip, security_id) values ('TSLA', 157001093);";
    private static final String DELETE_FROM_SECURITIES_REFERENCE_V_2 = "delete from SecuritiesReferenceV2";

    @Mock
    private Trade tradeMocked;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ApplicationPropertyUtils.readPropertiesFile("src/test/resources/test.application.properties");

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

    @AfterEach
    void cleanUp(){
        try {
            HibernateUtils.getInstance().startTransaction();
            String sql = "delete from Position";
            jakarta.persistence.Query query = HibernateUtils.getInstance().getConnection().createQuery(sql);
            query.executeUpdate();
            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            HibernateUtils.getInstance().rollbackTransaction();
        }

        try (PreparedStatement dropSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(DELETE_FROM_SECURITIES_REFERENCE_V_2)) {
            JDBCUtils.getInstance().startTransaction();

            dropSecRefTableStmt.executeUpdate();

            JDBCUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }

        ApplicationPropertyUtils.resetProperties();
    }


    @Test
    void getInstanceSingleThreadTest() {
        // Get two instances
        HibernatePositionsRepo instance1 = HibernatePositionsRepo.getInstance();
        HibernatePositionsRepo instance2 = HibernatePositionsRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<HibernatePositionsRepo> getInstance = HibernatePositionsRepo::getInstance;

        // Get two instances
        HibernatePositionsRepo instance1 = executorService.submit(getInstance).get();
        HibernatePositionsRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getVersionIdTradeNeverInsertedTest(){
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        int version = HibernatePositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);
        assertEquals(-1, version);
    }

    @Test
    void getVersionIdTradeAfterUpdateTest() throws OptimisticLockingOccurrence {
        // Setup
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        int version = HibernatePositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(trade);
        HibernateUtils.getInstance().commitTransaction();
        int versionAfterUpdate = HibernatePositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);

        // Assert
        assertEquals(version + 1, versionAfterUpdate);
    }

    private long getSizeOfTable(){
        String hql = "SELECT COUNT(e) FROM Position e";
        Query<Long> query = HibernateUtils.getInstance().getConnection().createQuery(hql, Long.class);
        return query.uniqueResult();
    }

    private List<Position> getEntriesInTable(){
        String hql = "SELECT e FROM Position e";
        Query<Position> query = HibernateUtils.getInstance().getConnection().createQuery(hql, Position.class);
        return query.getResultList();
    }

    @Test
    void positionUpdate_newPosition_buy_test() throws OptimisticLockingOccurrence {

        long sizeOfTableBeforeUpdate = getSizeOfTable();
        Trade trade = TestDataProvider.goodBuyTradeSupplier.get();

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();
        List<Position> dataInTableAfterUpdate = getEntriesInTable();

        Position position = Position.builder()
                .positionID(new PositionCompositeKey(trade.getAccountNumber(), 157001093))
                .version(0)
                .positionAmount(trade.getQuantity())
                .build();

        // Assert
        assertEquals(sizeOfTableBeforeUpdate + 1, sizeOfTableAfterUpdate);
        assertEquals(position, dataInTableAfterUpdate.get(0));
    }

    @Test
    void positionUpdate_newPosition_sell_test() throws OptimisticLockingOccurrence {
        long sizeOfTableBeforeUpdate = getSizeOfTable();
        Trade trade = TestDataProvider.goodSellTradeSupplier.get();

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();
        List<Position> dataInTableAfterUpdate = getEntriesInTable();

        Position position = Position.builder()
                .positionID(new PositionCompositeKey(trade.getAccountNumber(), 157001093))
                .version(0)
                .positionAmount(-trade.getQuantity())
                .build();

        // Assert
        assertEquals(sizeOfTableBeforeUpdate + 1, sizeOfTableAfterUpdate);
        assertEquals(position, dataInTableAfterUpdate.get(0));
    }

    @Test
    void positionUpdate_newPosition_invalidActivity_test() throws OptimisticLockingOccurrence {
        System.setOut(new PrintStream(outputStreamCaptor));

        Trade trade = TestDataProvider.invalidActivityTradeSupplier.get();
        long sizeOfTableBeforeUpdate = getSizeOfTable();

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();

        // Assert
        assertEquals(sizeOfTableBeforeUpdate, sizeOfTableAfterUpdate);
        assertTrue(outputStreamCaptor.toString().contains("UnrecognisedActivityOperationException"));

        System.setOut(originalOut);
    }

    @Test
    void positionUpdate_updatePosition_buy_test() throws OptimisticLockingOccurrence {
        Trade trade = TestDataProvider.goodBuyTradeSupplier.get();
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTable = getSizeOfTable();

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(trade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();
        List<Position> dataInTableAfterUpdate = getEntriesInTable();

        Position position = Position.builder()
                .positionID(new PositionCompositeKey(trade.getAccountNumber(), 157001093))
                .version(1) //  Version is Increased by 1
                .positionAmount(trade.getQuantity() * 2) // Since Buy, Quantity is Increased
                .build();

        // Assert
        assertEquals(sizeOfTable, sizeOfTableAfterUpdate);
        assertEquals(position, dataInTableAfterUpdate.get(0));
    }

    @Test
    void positionUpdate_updatePosition_sell_test() throws OptimisticLockingOccurrence {
        Trade buyTrade = TestDataProvider.goodBuyTradeSupplier.get();
        Trade sellTrade = TestDataProvider.goodSellTradeSupplier.get();
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(buyTrade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTable = getSizeOfTable();

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(sellTrade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();
        List<Position> dataInTableAfterUpdate = getEntriesInTable();

        Position position = Position.builder()
                .positionID(new PositionCompositeKey(buyTrade.getAccountNumber(), 157001093))
                .version(1) //  Version is Increased by 1
                .positionAmount(0) // Since Sell after Buy of same amount, Quantity is 0
                .build();

        // Assert
        assertEquals(sizeOfTable, sizeOfTableAfterUpdate);
        assertEquals(position, dataInTableAfterUpdate.get(0));
    }

    @Test
    void positionUpdate_updatePosition_invalidActivity_test() throws OptimisticLockingOccurrence {
        System.setOut(new PrintStream(outputStreamCaptor));

        Trade buyTrade = TestDataProvider.goodBuyTradeSupplier.get();
        Trade invalidTrade = TestDataProvider.invalidActivityTradeSupplier.get();
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(buyTrade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTable = getSizeOfTable();

        // Action
        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(invalidTrade);
        HibernateUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();
        List<Position> dataInTableAfterUpdate = getEntriesInTable();

        Position position = Position.builder()
                .positionID(new PositionCompositeKey(buyTrade.getAccountNumber(), 157001093))
                .version(0) //  Version is same as no change
                .positionAmount(buyTrade.getQuantity()) // Position Stays the same as well
                .build();

        // Assert
        assertEquals(sizeOfTable, sizeOfTableAfterUpdate);
        assertEquals(position, dataInTableAfterUpdate.get(0));

        assertTrue(outputStreamCaptor.toString().contains("UnrecognisedActivityOperationException..."));

        System.setOut(originalOut);
    }

    @Test
    void positionUpdate_updateFailed_test() throws OptimisticLockingOccurrence {
        System.setOut(new PrintStream(outputStreamCaptor));

        doAnswer(invocationOnMock -> {
            throw new Exception();
        }).when(tradeMocked).getCusip();

        HibernateUtils.getInstance().startTransaction();
        HibernatePositionsRepo.getInstance().updatePositionsTable(tradeMocked);
        HibernateUtils.getInstance().commitTransaction();

        assertTrue(outputStreamCaptor.toString().contains("Failed to Update Position"));

        System.setOut(originalOut);
    }


}
