package io.reactivestax.repo;

import io.reactivestax.model.Trade;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.reactivestax.utility.MultithreadTradeProcessorUtility.dataSource;

public class PayloadDatabaseRepo {

    public void writeToDatabase(String tradeID, String status, String payload){
        String query = "Insert into trades_payload (trade_id, status, payload, posted_status) values (?,?,?, 'Not Posted')";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement psQuery = connection.prepareStatement(query)){

            psQuery.setString(1,tradeID);
            psQuery.setString(2,status);
            psQuery.setString(3,payload);
            psQuery.executeUpdate();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public String readPayloadFromDB(String tradeID){
        String query = "Select payload from trades_payload where trade_id=?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement psQuery = connection.prepareStatement(query)){

            psQuery.setString(1, tradeID);
            ResultSet rsQuery = psQuery.executeQuery();
            rsQuery.next();
            return rsQuery.getString("payload");

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void updateSecurityLookupStatus(Trade trade, String validityStatus){
        String lookupUpdateQuery = "Update trades_payload set lookup_status = ? where trade_id = ?";

        try(Connection connection = dataSource.getConnection();
        PreparedStatement psLookupQuery = connection.prepareStatement(lookupUpdateQuery)){

            psLookupQuery.setString(2, trade.getTradeID());
            if(validityStatus.equals("Valid")){
                psLookupQuery.setString(1,"Succeeded");
            } else {psLookupQuery.setString(1, "Failed");}

            psLookupQuery.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateJournalEntryStatus(Trade trade){
        String updateJEQuery = "Update trades_payload set posted_status = 'Posted' where trade_id = ?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement updateJEps = connection.prepareStatement(updateJEQuery)){

            updateJEps.setString(1, trade.getTradeID());

            updateJEps.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
