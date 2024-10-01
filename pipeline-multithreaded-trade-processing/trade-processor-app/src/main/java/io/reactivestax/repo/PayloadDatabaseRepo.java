package io.reactivestax.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static io.reactivestax.utility.MultithreadTradeProcessorUtility.dataSource;

public class PayloadDatabaseRepo {

    public void writeToDatabase(String tradeID, String status, String payload){
        String query = "Insert into trades_payload (trade_id, status, payload) values (?,?,?)";

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
}
