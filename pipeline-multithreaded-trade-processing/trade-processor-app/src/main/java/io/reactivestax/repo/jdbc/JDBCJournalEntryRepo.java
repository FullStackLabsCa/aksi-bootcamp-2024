package io.reactivestax.repo.jdbc;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.JournalEntryRepo;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class JDBCJournalEntryRepo implements JournalEntryRepo {

    private JDBCJournalEntryRepo instance;
    private static final String WRITE_TO_JOURNAL_ENTRY_QUERY = """
                insert into journal_entry (account_number, security_id, direction, quantity, position_posted_status, trade_execution_time, trade_id)
                values (?,?,?,?,?,?,?)
                """;
    private static final String UPDATE_JE_POSITION_POSTING_STATUS_QUERY = "update journal_entry set position_posted_status = ? where trade_id = ?";

    private JDBCJournalEntryRepo(){
        //Private Constructor to avoid anyone creating instance of this Class
    }

    public JDBCJournalEntryRepo getInstance(){
        if(instance == null) instance = new JDBCJournalEntryRepo();
        return instance;
    }

    @Override
    public void writeTradeToJournalEntryTable(Trade trade) {
        try (PreparedStatement insertionQuery = connection.prepareStatement(WRITE_TO_JOURNAL_ENTRY_QUERY)) {
            insertionQuery.setString(1, trade.getAccountNumber());
            insertionQuery.setInt(2, getSecurityIdForCusip(connection, trade.getCusip()));
            insertionQuery.setString(3, trade.getActivity());
            insertionQuery.setInt(4, trade.getQuantity());
            insertionQuery.setString(5, "Not Posted");
            insertionQuery.setTimestamp(6, new Timestamp(trade.getTransactionTime().getTime()));
            insertionQuery.setString(7, trade.getTradeID());

            insertionQuery.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateJournalEntryForPositionUpdateStatus(Trade trade) {
        try (PreparedStatement psUpdateJe = connection.prepareStatement(UPDATE_JE_POSITION_POSTING_STATUS_QUERY)) {
            psUpdateJe.setString(1, "Posted");
            psUpdateJe.setString(2, trade.getTradeID());

            psUpdateJe.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
