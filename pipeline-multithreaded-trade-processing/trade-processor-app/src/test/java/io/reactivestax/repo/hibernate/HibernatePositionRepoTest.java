package io.reactivestax.repo.hibernate;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.HibernateUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;

public class HibernatePositionRepoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Mock
    private Trade tradeMocked;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @After
    public void cleanUp(){
        try {
            HibernateUtils.getInstance().startTransaction();
            String sql = "delete from Position";
            jakarta.persistence.Query query = HibernateUtils.getInstance().getConnection().createQuery(sql);
            query.executeUpdate();
            HibernateUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            HibernateUtils.getInstance().rollbackTransaction();
        }
    }


    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        HibernatePositionsRepo instance1 = HibernatePositionsRepo.getInstance();
        HibernatePositionsRepo instance2 = HibernatePositionsRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
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
    public void getVersionIdTradeNeverInsertedTest(){
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        int version = HibernatePositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);
        assertEquals(-1, version);
    }

    @Test
    public void getVersionIdTradeAfterUpdateTest() throws OptimisticLockingExceptionThrowable {
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
    public void positionUpdate_newPosition_buy_test() throws OptimisticLockingExceptionThrowable {

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
    public void positionUpdate_newPosition_sell_test() throws OptimisticLockingExceptionThrowable {
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
    public void positionUpdate_newPosition_invalidActivity_test() throws OptimisticLockingExceptionThrowable {
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
    public void positionUpdate_updatePosition_buy_test() throws OptimisticLockingExceptionThrowable {
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
    public void positionUpdate_updatePosition_sell_test() throws OptimisticLockingExceptionThrowable {
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
    public void positionUpdate_updatePosition_invalidActivity_test() throws OptimisticLockingExceptionThrowable {
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
    public void positionUpdate_updateFailed_test() throws OptimisticLockingExceptionThrowable {
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
