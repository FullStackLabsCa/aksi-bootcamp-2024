package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import io.reactivestax.utility.database.JDBCUtils;
import org.junit.After;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JDBCRawPayloadRepoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String INSERT_INTO_TRADES_PAYLOAD_QUERY = "Insert into trades_payload (trade_id, status, payload, postedStatus) values (?,?,?, 'Not Posted')";

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

    @Test
    public void readPayloadFromRawPayload_TradeIdDoesNotExists_Test(){
        System.setOut(new PrintStream(outputStreamCaptor));

        // Read the Payload for a tradeId, It will be Optional.empty()
        Optional<String> payloadReadFromRawPayloadTable = JDBCRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("test-trade");

        assertTrue(outputStreamCaptor.toString().contains("Some Error Occurred in reading Payload from RawPayload Table"));
        assertEquals(Optional.empty(), payloadReadFromRawPayloadTable);

        System.setOut(originalOut);
    }

    @Test
    public void readPayloadFromRawPayload_tradeIdExists_Test(){
        String tradePayload = TestDataProvider.validTradePayloadSupplier.get();

        // Insert the Trade with Payload into the DB
        insertIntoRawPayloadTable("TDB_00000001", tradePayload);

        // Read the payload for the same trade ID, it will be the same as that of Inserted
        Optional<String> payloadReadFromRawPayloadTable = JDBCRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("TDB_00000001");
        assertEquals(tradePayload, payloadReadFromRawPayloadTable.get());
    }

    public void insertIntoRawPayloadTable(String tradeID, String payload) {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement psQuery = connection.prepareStatement(INSERT_INTO_TRADES_PAYLOAD_QUERY)) {
            psQuery.setString(1, tradeID);
            psQuery.setString(2, "Valid");
            psQuery.setString(3, payload);
            psQuery.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
