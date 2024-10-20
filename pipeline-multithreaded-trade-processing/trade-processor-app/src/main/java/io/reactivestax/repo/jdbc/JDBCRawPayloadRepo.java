package io.reactivestax.repo.jdbc;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.RawPayloadRepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCRawPayloadRepo implements RawPayloadRepo {
    private static final String INSERT_INTO_TRADES_PAYLOAD_QUERY = "Insert into trades_payload (trade_id, status, payload, posted_status) values (?,?,?, 'Not Posted')";
    private static final String READ_PAYLOAD_QUERY = "Select payload from trades_payload where trade_id=?";
    private static final String LOOKUP_UPDATE_QUERY = "Update trades_payload set lookup_status = ? where trade_id = ?";
    private static final String UPDATE_JE_QUERY = "Update trades_payload set posted_status = 'Posted' where trade_id = ?";
    private Connection connection = null;
    private static JDBCRawPayloadRepo instance;

    private JDBCRawPayloadRepo() {
        //Private Constructor to avoid anyone creating instance of this Class
    }

    public static synchronized JDBCRawPayloadRepo getInstance(){
        if(instance == null) instance = new JDBCRawPayloadRepo();
        return instance;
    }

    @Override
    public void writeToRawPayloadTable(String tradeID, String payload, String validityStatus) {
        try (PreparedStatement psQuery = connection.prepareStatement(INSERT_INTO_TRADES_PAYLOAD_QUERY)) {

            psQuery.setString(1, tradeID);
            psQuery.setString(2, validityStatus);
            psQuery.setString(3, payload);
            psQuery.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String readPayloadFromRawPayloadsTable(String tradeID) {
        try (PreparedStatement psQuery = connection.prepareStatement(READ_PAYLOAD_QUERY)) {

            psQuery.setString(1, tradeID);
            ResultSet rsQuery = psQuery.executeQuery();
            rsQuery.next();
            return rsQuery.getString("payload");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public void updateSecurityLookupStatusInRawPayloadsTable(Trade trade, String lookupStatus) {
        try (PreparedStatement psLookupQuery = connection.prepareStatement(LOOKUP_UPDATE_QUERY)) {

            psLookupQuery.setString(2, trade.getTradeID());
            if (lookupStatus.equals("Valid")) {
                psLookupQuery.setString(1, "Succeeded");
            } else {
                psLookupQuery.setString(1, "Failed");
            }

            psLookupQuery.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateJournalEntryStatusInRawPayloadsTable(Trade trade) {
        try (PreparedStatement updateJEps = connection.prepareStatement(UPDATE_JE_QUERY)) {
            updateJEps.setString(1, trade.getTradeID());
            updateJEps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
