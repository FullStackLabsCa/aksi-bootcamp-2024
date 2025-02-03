package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.JournalEntry;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.ApplicationPropertyUtils;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.UpdatePositionStatusInJournalEntryFailed;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class JDBCJournalEntryRepoTest {

    private static final String CREATE_TABLE_JOURNAL_ENTRY_JOURNAL = """
                create table if not exists journal_entry (
                      journal_entry_id int auto_increment primary key,
                      accountNumber varchar(255) not null,
                      security_id int not null,
                      direction varchar(255) not null,
                      quantity int not null,
                      tradeExecutionTime date not null,
                      positionPostedStatus varchar(255) not null,
                      trade_id varchar(255) not null
                  );""";

    private static final String CREATE_TABLE_SECURITIES_REFERENCE = """
            create table if not exists SecuritiesReferenceV2 (
                    cusip varchar(15) not null unique,
                    security_id int not null unique
            );""";

    private static final String POPULATE_TABLE_SECURITIES_REFERENCE = "insert into SecuritiesReferenceV2 (cusip, security_id) values ('TSLA', 157001093);";

    private static final String DELETE_FROM_JOURNAL_ENTRY = "delete from journal_entry";
    private static final String DELETE_FROM_SECURITIES_REFERENCE_V_2 = "delete from SecuritiesReferenceV2";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ApplicationPropertyUtils.readPropertiesFile("src/test/resources/test.application.properties");

        try (PreparedStatement createJournalTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(CREATE_TABLE_JOURNAL_ENTRY_JOURNAL);
             PreparedStatement createSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(CREATE_TABLE_SECURITIES_REFERENCE)) {

            JDBCUtils.getInstance().startTransaction();
            createJournalTableStmt.executeUpdate();
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
        try (PreparedStatement dropJournalTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(DELETE_FROM_JOURNAL_ENTRY);
             PreparedStatement dropSecRefTableStmt = JDBCUtils.getInstance().getConnection().prepareStatement(DELETE_FROM_SECURITIES_REFERENCE_V_2)) {
            JDBCUtils.getInstance().startTransaction();

            dropJournalTableStmt.executeUpdate();
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
        JDBCJournalEntryRepo instance1 = JDBCJournalEntryRepo.getInstance();
        JDBCJournalEntryRepo instance2 = JDBCJournalEntryRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<JDBCJournalEntryRepo> getInstance = JDBCJournalEntryRepo::getInstance;

        // Get two instances
        JDBCJournalEntryRepo instance1 = executorService.submit(getInstance).get();
        JDBCJournalEntryRepo instance2 = executorService.submit(getInstance).get();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    private long getSizeOfTable() {
        long count = 0;
        String sql = "SELECT COUNT(*) FROM journal_entry";
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

    private List<JournalEntry> getEntriesInTable() {
        List<JournalEntry> journalEntry = new ArrayList<>();
        String sql = "SELECT * FROM journal_entry";

        try (PreparedStatement preparedStatement = JDBCUtils.getInstance().getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                JournalEntry position = JournalEntry.builder()
                        .accountNumber(resultSet.getString("accountNumber"))
                        .activity(resultSet.getString("direction"))
                        .positionPostedStatus(resultSet.getString("positionPostedStatus"))
                        .quantity(resultSet.getInt("quantity"))
                        .securityID(resultSet.getInt("security_id"))
                        .tradeExecutionTime(new java.sql.Date(resultSet.getTimestamp("tradeExecutionTime").getTime()))
                        .tradeID(resultSet.getString("trade_id"))
                        .build();
                journalEntry.add(position);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return journalEntry;
    }

    @Test
    void writeTradeToJournalEntryTableSizeTest() throws WriteToJournalEntryFailed {
        // Get the size of table before writing
        // should be 0
        long countBeforeInsertion = getSizeOfTable();

        assertEquals(0, countBeforeInsertion);

        // Create Trade
        Trade trade = TestDataProvider.goodTradeSupplier.get();

        // Write Trade to Journal Entry Table
        JDBCUtils.getInstance().startTransaction();
        JDBCJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);
        JDBCUtils.getInstance().commitTransaction();

        // Get the size of table after writing
        // should be 1
        long countAfterInsertion = getSizeOfTable();

        assertEquals(1, countAfterInsertion);
    }

    @Test
    void writeTradeToJournalEntryTableDataTest() throws WriteToJournalEntryFailed {
        // Get the data of table before writing
        // should be null
        List<JournalEntry> entriesBeforeInsertion = getEntriesInTable();

        assertEquals(0, entriesBeforeInsertion.size());

        // Create Trade
        Trade trade = TestDataProvider.goodTradeSupplier.get();

        // Write Trade to Journal Entry Table
        JDBCUtils.getInstance().startTransaction();
        JDBCJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);
        JDBCUtils.getInstance().commitTransaction();

        // Get the data of table after writing
        // match with the inserted data
        List<JournalEntry> entriesAfterInsertion = getEntriesInTable();

        JournalEntry expectedJournalEntry = JournalEntry.builder()
                .accountNumber(trade.getAccountNumber())
                .activity(trade.getActivity())
                .positionPostedStatus("Not Posted")
                .quantity(trade.getQuantity())
                .securityID(157001093)
                .tradeExecutionTime(trade.getTransactionTime())
                .tradeID(trade.getTradeID())
                .build();

        assertEquals(1, entriesAfterInsertion.size());
        assertEquals(expectedJournalEntry, entriesAfterInsertion.get(0));
    }

    @Test
    void writeTradeToJournalEntryFailedTest() {
        assertThrows(WriteToJournalEntryFailed.class, () -> JDBCJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(null));
    }

    @Test
    void updatePositionPostedStatusInJournalEntrySuccessfulTest() throws WriteToJournalEntryFailed, UpdatePositionStatusInJournalEntryFailed {
        // create trade
        Trade trade = TestDataProvider.goodTradeSupplier.get();

        // Insert Data into the database
        JDBCUtils.getInstance().startTransaction();
        JDBCJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(trade);
        JDBCUtils.getInstance().commitTransaction();

        // Check the posted Status - it should be non-posted
        List<JournalEntry> entriesInJournalEntryTable = getEntriesInTable();

        JournalEntry journalEntryFromDB = entriesInJournalEntryTable.get(0);
        assertEquals("Not Posted", journalEntryFromDB.getPositionPostedStatus());

        // call updateJournalEntryForPositionUpdate
        JDBCUtils.getInstance().startTransaction();
        JDBCJournalEntryRepo.getInstance().updatePositionPostedStatusInJournalEntry(trade);
        JDBCUtils.getInstance().commitTransaction();

        // the posted status now will be posted
        List<JournalEntry> entriesInJournalEntryTableAfterUpdate = getEntriesInTable();

        JournalEntry journalEntryFromDBAfterUpdate = entriesInJournalEntryTableAfterUpdate.get(0);
        assertEquals("Posted", journalEntryFromDBAfterUpdate.getPositionPostedStatus());
    }

    @Test
    void updatePositionPostedStatusInJournalEntryFailedTest() {
        assertThrows(UpdatePositionStatusInJournalEntryFailed.class, () -> JDBCJournalEntryRepo.getInstance().updatePositionPostedStatusInJournalEntry(null));
    }
}
