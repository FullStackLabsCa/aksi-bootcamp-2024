package io.reactivestax.repo.jdbc;

import io.reactivestax.model.Trade;
import io.reactivestax.repo.interfaces.SecuritiesReferenceRepo;
import io.reactivestax.utility.database.JDBCUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class JDBCSecuritiesReferenceRepo implements SecuritiesReferenceRepo {
    private static final String CUSIP_LOOKUP_QUERY = "select 1 from SecuritiesReferenceV2 where cusip = ?";
    private static final String LOOKUP_SECURITY_ID_QUERY = "Select security_id from SecuritiesReferenceV2 where cusip = ?";
    private static JDBCSecuritiesReferenceRepo instance;

    private JDBCSecuritiesReferenceRepo() {
        //Private Constructor to avoid anyone creating instance of this Class
    }

    public static synchronized JDBCSecuritiesReferenceRepo getInstance(){
        if(instance == null) instance = new JDBCSecuritiesReferenceRepo();
        return instance;
    }

    @Override
    public String checkIfValidCusip(Trade trade) {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement psLookUp = connection.prepareStatement(CUSIP_LOOKUP_QUERY)) {
            psLookUp.setString(1, trade.getCusip());
            ResultSet rsLookUp = psLookUp.executeQuery();

            if (rsLookUp.next())
                return "Valid";
            else return "Invalid";

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "Unable to Check CUSIP.";
    }

    @Override
    public int getSecurityIdForCusip(String cusip) {
        Connection connection = JDBCUtils.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(LOOKUP_SECURITY_ID_QUERY)) {

            ps.setString(1, cusip);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt("security_id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }
}
