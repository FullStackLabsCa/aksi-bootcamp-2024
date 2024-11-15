package io.reactivestax.repo.jdbc;

import io.reactivestax.TestDataProvider;
import io.reactivestax.entity.JournalEntry;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.WriteToJournalEntryFailed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class JDBCJournalEntryRepoTest {

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
        String sql = "delete from journal_entry";
        try (PreparedStatement preparedStatement = JDBCUtils.getInstance().getConnection().prepareStatement(sql)) {
            JDBCUtils.getInstance().startTransaction();

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Deleted " + rowsAffected + " rows from journal_entry table.");

            JDBCUtils.getInstance().commitTransaction();
        } catch (Exception e) {
            JDBCUtils.getInstance().rollbackTransaction();
        }
    }

    @Test
    public void getInstanceSingleThreadTest() {
        // Get two instances
        JDBCJournalEntryRepo instance1 = JDBCJournalEntryRepo.getInstance();
        JDBCJournalEntryRepo instance2 = JDBCJournalEntryRepo.getInstance();

        // Hashcode will be same
        assertEquals(instance2.hashCode(), instance1.hashCode());

        // Hashcode Identity will be same (reference to the same object)
        assertEquals(System.identityHashCode(instance1), System.identityHashCode(instance2));
    }

    @Test
    public void getInstanceMultiThreadTest() throws ExecutionException, InterruptedException {
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
    public void writeTradeToJournalEntryTableSizeTest() throws WriteToJournalEntryFailed {
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
    public void writeTradeToJournalEntryTableDataTest() throws WriteToJournalEntryFailed {
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
                .positionPostedStatus("Non Posted")
                .quantity(trade.getQuantity())
                .securityID(157001093)
                .tradeExecutionTime(trade.getTransactionTime())
                .tradeID(trade.getTradeID())
                .build();

        assertEquals(1, entriesAfterInsertion.size());
        assertEquals(expectedJournalEntry, entriesAfterInsertion.get(0));
    }

    @Test
    public void writeTradeToJournalEntryFailedTest() {
        assertThrows(WriteToJournalEntryFailed.class, () -> JDBCJournalEntryRepo.getInstance().writeTradeToJournalEntryTable(null));
    }
}
