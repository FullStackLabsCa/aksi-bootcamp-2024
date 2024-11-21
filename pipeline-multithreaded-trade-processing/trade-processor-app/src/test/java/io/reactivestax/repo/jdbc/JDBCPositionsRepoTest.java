package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;

public class JDBCPositionsRepoTest {

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
            String sql = "delete from positions";
        try (PreparedStatement preparedStatement =JDBCUtils.getInstance().getConnection().prepareStatement(sql)) {
            JDBCUtils.getInstance().startTransaction();

                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println("Deleted " + rowsAffected + " rows from Position table.");

            JDBCUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }
    }

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCPositionsRepo instance1 = JDBCPositionsRepo.getInstance();
        JDBCPositionsRepo instance2 = JDBCPositionsRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCPositionsRepo> getInstance = JDBCPositionsRepo::getInstance;

        // Get two instances
        JDBCPositionsRepo instance1 = executorService.submit(getInstance).get();
        JDBCPositionsRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getVersionIdTradeNeverInsertedTest() {
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        int version = JDBCPositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);
        assertEquals(-1, version);
    }

    @Test
    public void getVersionIdTradeAfterUpdateTest() throws OptimisticLockingException {
        // Setup
        Trade trade = TestDataProvider.goodTradeSupplier.get();
        int version = JDBCPositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);

        // Action
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(trade);
        JDBCUtils.getInstance().commitTransaction();
        int versionAfterUpdate = JDBCPositionsRepo.getInstance().getVersionIdForPosition(trade, 157001093);

        // Assert
        assertEquals(version + 1, versionAfterUpdate);
    }

    @Test
    public void getVersionIdFailedTest() throws OptimisticLockingException {
        System.setOut(new PrintStream(outputStreamCaptor));

        doAnswer(invocationOnMock -> {
            throw new SQLException();
        }).when(tradeMocked).getAccountNumber();

        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().getVersionIdForPosition(tradeMocked, 0);
        JDBCUtils.getInstance().commitTransaction();

        assertTrue(outputStreamCaptor.toString().contains("Failed to Get Version ID for Position."));

        System.setOut(originalOut);
    }

    private long getSizeOfTable() {
        long count = 0;
        String sql = "SELECT COUNT(*) FROM positions";
        try (PreparedStatement preparedStatement = JDBCUtils.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return count;
    }

    private List<Position> getEntriesInTable() {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM positions";

        try (PreparedStatement preparedStatement = JDBCUtils.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Position position = Position.builder()
                        .positionID(new PositionCompositeKey(resultSet.getString("account_number"), resultSet.getInt("security_id")))
                        .positionAmount(resultSet.getInt("position"))
                        .version(resultSet.getInt("version"))
                        .build();
                positions.add(position);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return positions;
    }

    @Test
    public void positionUpdate_newPosition_buy_test() throws OptimisticLockingException {

        long sizeOfTableBeforeUpdate = getSizeOfTable();
        Trade trade = TestDataProvider.goodBuyTradeSupplier.get();

        // Action
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(trade);
        JDBCUtils.getInstance().commitTransaction();

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
    public void positionUpdate_newPosition_sell_test() throws OptimisticLockingException {
        long sizeOfTableBeforeUpdate = getSizeOfTable();
        Trade trade = TestDataProvider.goodSellTradeSupplier.get();

        // Action
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(trade);
        JDBCUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();
        List<Position> dataInTableAfterUpdate = getEntriesInTable();

        Position position = Position.builder()
                .positionID(new PositionCompositeKey(trade.getAccountNumber(), 157001093))
                .version(0)
                .positionAmount(trade.getQuantity() * -1)
                .build();

        // Assert
        assertEquals(sizeOfTableBeforeUpdate + 1, sizeOfTableAfterUpdate);
        assertEquals(position, dataInTableAfterUpdate.get(0));
    }

    @Test
    public void positionUpdate_newPosition_invalidActivity_test() throws OptimisticLockingException {
        System.setOut(new PrintStream(outputStreamCaptor));

        Trade trade = TestDataProvider.invalidActivityTradeSupplier.get();
        long sizeOfTableBeforeUpdate = getSizeOfTable();

        // Action
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(trade);
        JDBCUtils.getInstance().commitTransaction();

        long sizeOfTableAfterUpdate = getSizeOfTable();

        // Assert
        assertEquals(sizeOfTableBeforeUpdate, sizeOfTableAfterUpdate);
        assertTrue(outputStreamCaptor.toString().contains("UnrecognisedActivityOperationException"));

        System.setOut(originalOut);
    }

    @Test
    public void positionUpdate_updatePosition_buy_test() throws OptimisticLockingException {
        Trade trade = TestDataProvider.goodBuyTradeSupplier.get();
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(trade);
        JDBCUtils.getInstance().commitTransaction();

        long sizeOfTable = getSizeOfTable();

        // Action
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(trade);
        JDBCUtils.getInstance().commitTransaction();

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
    public void positionUpdate_updatePosition_sell_test() throws OptimisticLockingException {
        Trade buyTrade = TestDataProvider.goodBuyTradeSupplier.get();
        Trade sellTrade = TestDataProvider.goodSellTradeSupplier.get();
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(buyTrade);
        JDBCUtils.getInstance().commitTransaction();

        long sizeOfTable = getSizeOfTable();

        // Action
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(sellTrade);
        JDBCUtils.getInstance().commitTransaction();

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
    public void positionUpdate_updatePosition_invalidActivity_test() throws OptimisticLockingException {
        System.setOut(new PrintStream(outputStreamCaptor));

        Trade buyTrade = TestDataProvider.goodBuyTradeSupplier.get();
        Trade invalidTrade = TestDataProvider.invalidActivityTradeSupplier.get();
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(buyTrade);
        JDBCUtils.getInstance().commitTransaction();

        long sizeOfTable = getSizeOfTable();

        // Action
        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(invalidTrade);
        JDBCUtils.getInstance().commitTransaction();

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

        assertTrue(outputStreamCaptor.toString().contains("UnrecognisedActivityOperationException"));

        System.setOut(originalOut);
    }

    @Test
    public void positionUpdate_updateFailed_test() throws OptimisticLockingException {
        System.setOut(new PrintStream(outputStreamCaptor));

        doAnswer(invocationOnMock -> {
            throw new SQLException();
        }).when(tradeMocked).getCusip();

        JDBCUtils.getInstance().startTransaction();
        JDBCPositionsRepo.getInstance().updatePositionsTable(tradeMocked);
        JDBCUtils.getInstance().commitTransaction();

        assertTrue(outputStreamCaptor.toString().contains("Failed to Update Position"));

        System.setOut(originalOut);
    }
}
