package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.UpdateJournalEntryStatusInRawPayloadFailed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class JDBCRawPayloadRepoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String INSERT_INTO_TRADES_PAYLOAD_QUERY = "Insert into trades_payload (trade_id, status, payload, lookupStatus, postedStatus) values (?,?,?, 'Not Posted', 'Not Posted')";
    private static final String READ_SECURITYLOOKUPSTATUS_QUERY = "Select lookupStatus from trades_payload where trade_id=?";
    private static final String READ_POSITIONUPDATESTATUS_QUERY = "Select postedStatus from trades_payload where trade_id=?";

    @AfterEach
    void cleanUp(){
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
    void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCRawPayloadRepo instance1 = JDBCRawPayloadRepo.getInstance();
        JDBCRawPayloadRepo instance2 = JDBCRawPayloadRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
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
    void readPayloadFromRawPayload_TradeIdDoesNotExists_Test(){
        System.setOut(new PrintStream(outputStreamCaptor));

        // Read the Payload for a tradeId, It will be Optional.empty()
        Optional<String> payloadReadFromRawPayloadTable = JDBCRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("test-trade");

        assertTrue(outputStreamCaptor.toString().contains("Some Error Occurred in reading Payload from RawPayload Table"));
        assertEquals(Optional.empty(), payloadReadFromRawPayloadTable);

        System.setOut(originalOut);
    }

    @Test
    void readPayloadFromRawPayload_tradeIdExists_Test(){
        String tradePayload = TestDataProvider.validTradePayloadSupplier.get();

        // Insert the Trade with Payload into the DB
        insertIntoRawPayloadTable("TDB_00000001", tradePayload);

        // Read the payload for the same trade ID, it will be the same as that of Inserted
        Optional<String> payloadReadFromRawPayloadTable = JDBCRawPayloadRepo.getInstance().readPayloadFromRawPayloadsTable("TDB_00000001");
        assertEquals(tradePayload, payloadReadFromRawPayloadTable.get());
    }

    void insertIntoRawPayloadTable(String tradeID, String payload) {
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

    @ParameterizedTest
    @MethodSource("provideLookUpStatusForTest")
    void updateSecurityLookUpStatusTest(String lookupStatus) throws SQLException {
        Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();
        insertIntoRawPayloadTable("TDB_00000001", TestDataProvider.validTradePayloadSupplier.get());

        JDBCRawPayloadRepo.getInstance().updateSecurityLookupStatusInRawPayloadsTable(trade, lookupStatus);
        String securityLookUpStatusAfterUpdate = readSecurityLookUpStatusFromRawPayload("TDB_00000001");

        if ("Valid".equals(lookupStatus))
            assertEquals("Succeeded", securityLookUpStatusAfterUpdate);
        else
            assertEquals("Failed", securityLookUpStatusAfterUpdate);
    }

    private String readSecurityLookUpStatusFromRawPayload(String tradeID) throws SQLException {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement psQuery = connection.prepareStatement(READ_SECURITYLOOKUPSTATUS_QUERY)) {

            psQuery.setString(1, tradeID);
            ResultSet rsQuery = psQuery.executeQuery();
            rsQuery.next();
            return rsQuery.getString("lookupStatus");
        }
    }

    static Stream<Arguments> provideLookUpStatusForTest() {
        return Stream.of(
                Arguments.of("Valid"),
                Arguments.of("Invalid")
        );
    }

    @Test
    void updateSecurityLookUpStatusFailedTest(){
        System.setOut(new PrintStream(outputStreamCaptor));

        JDBCRawPayloadRepo.getInstance().updateSecurityLookupStatusInRawPayloadsTable(null, null);
        assertTrue(outputStreamCaptor.toString().contains("Failed to Update Security Lookup Status in Raw-Payload Table"));

        System.setOut(originalOut);
    }

    private String readPostedStatusFromRawPayload(String tradeID) throws SQLException {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement psQuery = connection.prepareStatement(READ_POSITIONUPDATESTATUS_QUERY)) {

            psQuery.setString(1, tradeID);
            ResultSet rsQuery = psQuery.executeQuery();
            rsQuery.next();
            return rsQuery.getString("postedStatus");
        }
    }

    @Test
    void updateJournalEntryStatusTest() throws UpdateJournalEntryStatusInRawPayloadFailed, SQLException {
        Trade trade = TestDataProvider.validTradeForPayloadSupplier.get();
        insertIntoRawPayloadTable("TDB_00000001", TestDataProvider.validTradePayloadSupplier.get());

        String postedStatusBeforeUpdate = readPostedStatusFromRawPayload("TDB_00000001");
        assertEquals("Not Posted", postedStatusBeforeUpdate);

        JDBCRawPayloadRepo.getInstance().updateJournalEntryStatusInRawPayloadsTable(trade);

        String postedStatusAfterUpdate = readPostedStatusFromRawPayload("TDB_00000001");

        assertEquals("Posted", postedStatusAfterUpdate);
    }

    @Test
    void updateJournalEntryStatusFailedTest() {
        assertThrows(UpdateJournalEntryStatusInRawPayloadFailed.class, () -> JDBCRawPayloadRepo.getInstance().updateJournalEntryStatusInRawPayloadsTable(null));
    }
}
