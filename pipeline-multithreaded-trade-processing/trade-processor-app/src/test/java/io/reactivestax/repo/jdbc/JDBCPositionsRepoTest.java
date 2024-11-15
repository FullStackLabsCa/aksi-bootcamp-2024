package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.Position;
import io.reactivestax.entity.PositionCompositeKey;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingExceptionThrowable;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class JDBCPositionsRepoTest {


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
    public void getVersionIdTradeAfterUpdateTest() throws OptimisticLockingExceptionThrowable {
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

    private long getSizeOfTable() {
        long count = 0;
        String sql = "SELECT COUNT(*) FROM Position";
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
        String sql = "SELECT * FROM Position"; // Assuming 'Position' corresponds to the table name

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
}
