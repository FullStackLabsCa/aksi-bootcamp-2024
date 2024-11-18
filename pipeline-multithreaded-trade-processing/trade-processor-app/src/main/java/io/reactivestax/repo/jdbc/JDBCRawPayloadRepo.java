package io.reactivestax.repo.jdbc;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.UpdateJournalEntryStatusInRawPayloadFailed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JDBCRawPayloadRepo implements RawPayloadRepo {
    private static final String READ_PAYLOAD_QUERY = "Select payload from trades_payload where trade_id=?";
    private static final String LOOKUP_UPDATE_QUERY = "Update trades_payload set lookupStatus = ? where trade_id = ?";
    private static final String UPDATE_JE_QUERY = "Update trades_payload set postedStatus = 'Posted' where trade_id = ?";
    private static JDBCRawPayloadRepo instance;

    private JDBCRawPayloadRepo() {
        //Private Constructor to avoid anyone creating instance of this Class
    }

    public static synchronized JDBCRawPayloadRepo getInstance(){
        if(instance == null) instance = new JDBCRawPayloadRepo();
        return instance;
    }

    @Override
    public Optional<String> readPayloadFromRawPayloadsTable(String tradeID) {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement psQuery = connection.prepareStatement(READ_PAYLOAD_QUERY)) {

            psQuery.setString(1, tradeID);
            ResultSet rsQuery = psQuery.executeQuery();
            rsQuery.next();
            return Optional.ofNullable(rsQuery.getString("payload"));

        } catch (SQLException e) {
            System.out.println("Some Error Occurred in reading Payload from RawPayload Table");
        }

        return Optional.empty();
    }

    @Override
    public void updateSecurityLookupStatusInRawPayloadsTable(Trade trade, String lookupStatus) {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement psLookupQuery = connection.prepareStatement(LOOKUP_UPDATE_QUERY)) {

            psLookupQuery.setString(2, trade.getTradeID());
            if ("Valid".equals(lookupStatus)) {
                psLookupQuery.setString(1, "Succeeded");
            } else {
                psLookupQuery.setString(1, "Failed");
            }

            psLookupQuery.executeUpdate();
        } catch (Exception e) {
            System.out.println("Failed to Update Security Lookup Status in Raw-Payload Table");
        }
    }

    @Override
    public void updateJournalEntryStatusInRawPayloadsTable(Trade trade) throws UpdateJournalEntryStatusInRawPayloadFailed {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement updateJEps = connection.prepareStatement(UPDATE_JE_QUERY)) {
            updateJEps.setString(1, trade.getTradeID());
            updateJEps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Failed to Update Journal Entry Status in Raw-Payload Table");
            throw new UpdateJournalEntryStatusInRawPayloadFailed();
        }
    }
}
