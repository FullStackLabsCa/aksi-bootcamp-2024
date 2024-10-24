package io.reactivestax.repo.jdbc;

import io.reactivestax.repo.RawPayloadRepo;
import io.reactivestax.utility.database.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCRawPayloadRepo implements RawPayloadRepo {
    private static final String INSERT_INTO_TRADES_PAYLOAD_QUERY = "Insert into trades_payload (trade_id, status, payload, posted_status) values (?,?,?, 'Not Posted')";
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
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement psQuery = connection.prepareStatement(INSERT_INTO_TRADES_PAYLOAD_QUERY)) {
            psQuery.setString(1, tradeID);
            psQuery.setString(2, validityStatus);
            psQuery.setString(3, payload);
            psQuery.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
