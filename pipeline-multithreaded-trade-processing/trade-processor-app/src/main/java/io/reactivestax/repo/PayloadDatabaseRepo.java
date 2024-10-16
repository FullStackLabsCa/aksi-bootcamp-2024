package io.reactivestax.repo;

import io.reactivestax.entity.RawPayload;
import io.reactivestax.model.Trade;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PayloadDatabaseRepo {

    public void writeToDatabase(Connection connection, String tradeID, String status, String payload){
        String query = "Insert into trades_payload (trade_id, status, payload, posted_status) values (?,?,?, 'Not Posted')";

        try(PreparedStatement psQuery = connection.prepareStatement(query)){

            psQuery.setString(1,tradeID);
            psQuery.setString(2,status);
            psQuery.setString(3,payload);
            psQuery.executeUpdate();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public String readPayloadFromDB(Connection connection, String tradeID){
        String query = "Select payload from trades_payload where trade_id=?";

        try(PreparedStatement psQuery = connection.prepareStatement(query)){

            psQuery.setString(1, tradeID);
            ResultSet rsQuery = psQuery.executeQuery();
            rsQuery.next();
            return rsQuery.getString("payload");

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void updateSecurityLookupStatus(Connection connection, Trade trade, String validityStatus){
        String lookupUpdateQuery = "Update trades_payload set lookup_status = ? where trade_id = ?";

        try(PreparedStatement psLookupQuery = connection.prepareStatement(lookupUpdateQuery)){

            psLookupQuery.setString(2, trade.getTradeID());
            if(validityStatus.equals("Valid")){
                psLookupQuery.setString(1,"Succeeded");
            } else {psLookupQuery.setString(1, "Failed");}

            psLookupQuery.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void updateJournalEntryStatus(Connection connection, Trade trade){
        String updateJEQuery = "Update trades_payload set posted_status = 'Posted' where trade_id = ?";

        try(PreparedStatement updateJEps = connection.prepareStatement(updateJEQuery)){

            updateJEps.setString(1, trade.getTradeID());

            updateJEps.executeUpdate();

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //Using Hibernate
    public void writeToDatabaseUsingHibernate(Session session, String tradeID, String status, String payload){
        try {
            RawPayload rawPayload = new RawPayload();
            rawPayload.setTradeID(tradeID);
            rawPayload.setStatus(status);
            rawPayload.setPayload(payload);
            rawPayload.setLookupStatus("Non Posted");
            Transaction transaction;
            try {
                transaction = session.beginTransaction();
            } catch (Exception e) {
                transaction = session.getTransaction();
            }
            session.persist(rawPayload);
            transaction.commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
