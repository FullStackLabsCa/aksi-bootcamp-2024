package io.reactivestax.repo.jdbc;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.PositionsRepo;
import io.reactivestax.utility.database.JDBCUtils;
import io.reactivestax.utility.exceptions.OptimisticLockingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCPositionsRepo implements PositionsRepo {
    private static final String POSITION_INSERT_QUERY = "Insert into positions (account_number, security_id, position, version) values (?,?,?,0)";
    private static final String POSITION_UPDATE_QUERY = "update positions set position = (position + ?), version = (version + 1) where version = ?";
    private static final String GET_VERSION_ID = "SELECT version FROM positions WHERE account_number = ? and security_id = ?";
    private static JDBCPositionsRepo instance;

    private JDBCPositionsRepo() {
        //Private Constructor to avoid anyone creating instance of this Class
    }

    public static synchronized JDBCPositionsRepo getInstance(){
        if(instance == null) instance = new JDBCPositionsRepo();
        return instance;
    }

    @Override
    public void updatePositionsTable(Trade trade) {
        Connection connection = JDBCUtils.getInstance().getConnection();
        JDBCSecuritiesReferenceRepo securitiesReference = JDBCSecuritiesReferenceRepo.getInstance();
        JDBCPositionsRepo positionsReference = JDBCPositionsRepo.getInstance();

        int securityID = securitiesReference.getSecurityIdForCusip(trade.getCusip());
        int version = positionsReference.getVersionIdForPosition(trade, securityID);

        try (PreparedStatement psPositionInsertQuery = connection.prepareStatement(POSITION_INSERT_QUERY);
             PreparedStatement psPositionUpdateQuery = connection.prepareStatement(POSITION_UPDATE_QUERY)) {

            if (version == -1) {
                //Perform Insertion Logic
                psPositionInsertQuery.setString(1, trade.getAccountNumber());
                psPositionInsertQuery.setInt(2, securityID);

                if (trade.getActivity().equals("BUY")) {
                    psPositionInsertQuery.setInt(3, trade.getQuantity());
                } else if (trade.getActivity().equals("SELL")) {
                    psPositionInsertQuery.setInt(3, -trade.getQuantity());
                } else {
                    System.out.println("UnrecognisedActivityOperationException");
                }

                psPositionInsertQuery.executeUpdate();

            } else {
                //Perform Update Logic
                if (trade.getActivity().equals("BUY")) {
                    psPositionUpdateQuery.setInt(1, trade.getQuantity());
                } else if (trade.getActivity().equals("SELL")) {
                    psPositionUpdateQuery.setInt(1, -trade.getQuantity());
                } else {
                    System.out.println("UnrecognisedActivityOperationException");
                }
                psPositionUpdateQuery.setInt(2, version);

                if (psPositionUpdateQuery.executeUpdate() == 0)
                    throw new OptimisticLockingException("Optimistic Locking Occurring!!!!!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
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
        }
        return 0;
    }
}
