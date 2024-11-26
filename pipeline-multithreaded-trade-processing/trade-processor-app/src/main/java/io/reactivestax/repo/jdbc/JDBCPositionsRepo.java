package io.reactivestax.repo.jdbc;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.PositionsRepo;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingOccurrence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCPositionsRepo implements PositionsRepo {
    private static final String POSITION_INSERT_QUERY = "Insert into positions (account_number, security_id, position, version) values (?,?,?,0)";
    private static final String POSITION_UPDATE_QUERY = "update positions set position = (position + ?), version = (version + 1) where version = ?";
    private static final String GET_VERSION_ID = "SELECT version FROM positions WHERE account_number = ? and security_id = ?";
    private static final String BUY = "BUY";
    private static final String SELL = "SELL";
    private static final String UNRECOGNISED_ACTIVITY_OPERATION_EXCEPTION = "UnrecognisedActivityOperationException";
    private static JDBCPositionsRepo instance;

    private JDBCPositionsRepo() {
        //Private Constructor to avoid anyone creating instance of this Class
    }

    public static synchronized JDBCPositionsRepo getInstance(){
        if(instance == null) instance = new JDBCPositionsRepo();
        return instance;
    }

    @Override
    public void updatePositionsTable(Trade trade) throws OptimisticLockingOccurrence {
        Connection connection = JDBCUtils.getInstance().getConnection();
        JDBCSecuritiesReferenceRepo securitiesReference = JDBCSecuritiesReferenceRepo.getInstance();
        JDBCPositionsRepo positionsReference = JDBCPositionsRepo.getInstance();

        try (PreparedStatement psPositionInsertQuery = connection.prepareStatement(POSITION_INSERT_QUERY);
             PreparedStatement psPositionUpdateQuery = connection.prepareStatement(POSITION_UPDATE_QUERY)) {

            int securityID = securitiesReference.getSecurityIdForCusip(trade.getCusip());
            int version = positionsReference.getVersionIdForPosition(trade, securityID);

            if (version == -1) {
                //Perform Insertion Logic
                psPositionInsertQuery.setString(1, trade.getAccountNumber());
                psPositionInsertQuery.setInt(2, securityID);

                if (BUY.equals(trade.getActivity())) {
                    psPositionInsertQuery.setInt(3, trade.getQuantity());
                } else if (SELL.equals(trade.getActivity())) {
                    psPositionInsertQuery.setInt(3, -trade.getQuantity());
                } else {
                    System.out.println(UNRECOGNISED_ACTIVITY_OPERATION_EXCEPTION);
                    return;
                }

                psPositionInsertQuery.executeUpdate();

            } else {
                //Perform Update Logic
                if (BUY.equals(trade.getActivity())) {
                    psPositionUpdateQuery.setInt(1, trade.getQuantity());
                } else if (SELL.equals(trade.getActivity())) {
                    psPositionUpdateQuery.setInt(1, -trade.getQuantity());
                } else {
                    System.out.println(UNRECOGNISED_ACTIVITY_OPERATION_EXCEPTION);
                    return;
                }
                psPositionUpdateQuery.setInt(2, version);

                if (psPositionUpdateQuery.executeUpdate() == 0)
                    throw new OptimisticLockingOccurrence();
            }
        } catch (SQLException e) {
            System.out.println("Failed to Update Position");
        }
    }

    public int getVersionIdForPosition(Trade trade, int securityId) {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement stmt = connection.prepareStatement(GET_VERSION_ID)) {
            stmt.setString(1, trade.getAccountNumber());
            stmt.setInt(2, securityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("version");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to Get Version ID for Position.");
        }
        return 0;
    }
}
