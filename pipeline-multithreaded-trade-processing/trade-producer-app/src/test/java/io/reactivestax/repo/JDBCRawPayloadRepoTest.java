package io.reactivestax.repo;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.RawPayload;
import io.reactivestax.repo.jdbc.JDBCRawPayloadRepo;
import io.reactivestax.utility.database.JDBCUtils;
import org.junit.After;
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

public class JDBCRawPayloadRepoTest {

    @After
    public void cleanUp(){
        String sql = "delete from trades_payload";
        try (PreparedStatement preparedStatement = JDBCUtils.getInstance().getConnection().prepareStatement(sql)) {
            JDBCUtils.getInstance().startTransaction();

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " rows from trades_payload table.");

            JDBCUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }
    }

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCRawPayloadRepo instance1 = JDBCRawPayloadRepo.getInstance();
        JDBCRawPayloadRepo instance2 = JDBCRawPayloadRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCRawPayloadRepo> getInstance = JDBCRawPayloadRepo::getInstance;

        // Get two instances
        JDBCRawPayloadRepo instance1 = executorService.submit(getInstance).get();
        JDBCRawPayloadRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

//    @Test
//    public void writeToRawPayloadTest(){
//        long sizeOfTableBeforeInsertion = getSizeOfTable();
//
//        String tradeID = TestDataProvider.validTradeIdSupplier.get();
//        String payload = TestDataProvider.validTradePayloadSupplier.get();
//        String validStatus = "Valid";
//        JDBCRawPayloadRepo.getInstance().writeToRawPayloadTable(tradeID, payload, validStatus);
//
//        long sizeOfTableAfterInsertion = getSizeOfTable();
//        List<RawPayload> entriesInTableAfterInsertion = getEntriesInTable();
//
//        assertEquals(sizeOfTableBeforeInsertion + 1, sizeOfTableAfterInsertion);
//        assertEquals(tradeID, entriesInTableAfterInsertion.get(0).getTradeID());
//        assertEquals(payload, entriesInTableAfterInsertion.get(0).getPayload());
//        assertEquals(validStatus, entriesInTableAfterInsertion.get(0).getStatus());
//    }
//
////    private long getSizeOfTable(){
////        String hql = "SELECT COUNT(e) FROM RawPayload e";
////        Query<Long> query = JDBCUtils.getInstance().getConnection().createQuery(hql, Long.class);
////        return query.uniqueResult();
////    }
////
////    private List<RawPayload> getEntriesInTable(){
////        String hql = "SELECT e FROM RawPayload e";
////        Query<RawPayload> query = JDBCUtils.getInstance().getConnection().createQuery(hql, RawPayload.class);
////        return query.getResultList();
////    }
//
//    private long getSizeOfTable() {
//        long count = 0;
//        String sql = "SELECT COUNT(*) FROM journal_entry";
//        try (PreparedStatement preparedStatement = JDBCUtils.getInstance().getConnection().prepareStatement(sql);
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//            if (resultSet.next()) {
//                count = resultSet.getLong(1);
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//        return count;
//    }
//
//    private List<JournalEntry> getEntriesInTable() {
//        List<JournalEntry> journalEntry = new ArrayList<>();
//        String sql = "SELECT * FROM journal_entry";
//
//        try (PreparedStatement preparedStatement = JDBCUtils.getInstance().getConnection().prepareStatement(sql);
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//
//            while (resultSet.next()) {
//                JournalEntry position = JournalEntry.builder()
//                        .accountNumber(resultSet.getString("accountNumber"))
//                        .activity(resultSet.getString("direction"))
//                        .positionPostedStatus(resultSet.getString("positionPostedStatus"))
//                        .quantity(resultSet.getInt("quantity"))
//                        .securityID(resultSet.getInt("security_id"))
//                        .tradeExecutionTime(new java.sql.Date(resultSet.getTimestamp("tradeExecutionTime").getTime()))
//                        .tradeID(resultSet.getString("trade_id"))
//                        .build();
//                journalEntry.add(position);
//            }
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        return journalEntry;
//    }
}
