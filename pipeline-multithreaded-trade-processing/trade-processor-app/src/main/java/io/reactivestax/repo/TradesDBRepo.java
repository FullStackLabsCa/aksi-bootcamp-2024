package io.reactivestax.repo;

import io.reactivestax.model.Trade;
import io.reactivestax.utility.OptimisticLockingException;

import java.sql.*;

public class TradesDBRepo {

    public TradesDBRepo() {
        //I don't really know what to enter here. I am trying to resolve sonar lint warning here...
    }

    public String checkIfValidCUSIP(Trade trade, Connection connection){
        String lookupQuery = "select 1 from SecuritiesReferenceV2 where cusip = ?";

        try(PreparedStatement psLookUp = connection.prepareStatement(lookupQuery)){

            psLookUp.setString(1, trade.getCusip());
            ResultSet rsLookUp = psLookUp.executeQuery();

            if(rsLookUp.next())
                return "Valid";
            else return "Invalid";

        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return "Unable to Check CUSIP.";
    }

    public void writeTradeToJournalTable(Trade trade, Connection connection){
        String writeToJournalQuery = """
                insert into journal_entry (account_number, security_id, direction, quantity, position_posted_status, trade_execution_time, trade_id)
                values (?,?,?,?,?,?,?)
                """;

        try(PreparedStatement insertionQuery = connection.prepareStatement(writeToJournalQuery)){
            insertionQuery.setString(1,trade.getAccountNumber());
            insertionQuery.setInt(2,getSecurityIdForCusip(trade.getCusip(),connection));
            insertionQuery.setString(3,trade.getActivity());
            insertionQuery.setInt(4,trade.getQuantity());
            insertionQuery.setString(5,"Not Posted");
            insertionQuery.setTimestamp(6, new Timestamp(trade.getTransactionTime().getTime()));
            insertionQuery.setString(7, trade.getTradeID());

            insertionQuery.executeUpdate();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public int getSecurityIdForCusip(String cusip, Connection connection){

        String lookUpSecurityQuery = "Select security_id from SecuritiesReferenceV2 where cusip = ?";

        try(PreparedStatement ps = connection.prepareStatement(lookUpSecurityQuery)){

            ps.setString(1, cusip);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt("security_id");

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return 0;
    }

    public void updatePositionsTable(Trade trade, Connection connection) throws OptimisticLockingException{
        int securityID = getSecurityIdForCusip(trade.getCusip(), connection);
        int version = getAccountVersion(trade, securityID, connection);

        String positionInsertQuery = "Insert into positions (account_number, security_id, position, version) values (?,?,?,0)";
        String positionUpdateQuery = "update positions set position = (position + ?), version = (version + 1) where version = ?";

        try(PreparedStatement psPositionInsertQuery = connection.prepareStatement(positionInsertQuery);
                PreparedStatement psPositionUpdateQuery = connection.prepareStatement(positionUpdateQuery)){

            if(version == -1) {
                //Perform Insertion Logic
                psPositionInsertQuery.setString(1,trade.getAccountNumber());
                psPositionInsertQuery.setInt(2, securityID);

                if(trade.getActivity().equals("BUY")){
                    psPositionInsertQuery.setInt(3, trade.getQuantity());
                }else if(trade.getActivity().equals("SELL")){
                    psPositionInsertQuery.setInt(3, -trade.getQuantity());
                } else {
                    System.out.println("UnrecognisedActivityOperationException");
                }

                psPositionInsertQuery.executeUpdate();

            } else {
                //Perform Update Logic
                if(trade.getActivity().equals("BUY")){
                    psPositionUpdateQuery.setInt(1, trade.getQuantity());
                }else if(trade.getActivity().equals("SELL")){
                    psPositionUpdateQuery.setInt(1, -trade.getQuantity());
                } else {
                    System.out.println("UnrecognisedActivityOperationException");
                }
                psPositionUpdateQuery.setInt(2, version);

                if(psPositionUpdateQuery.executeUpdate() == 0) throw new OptimisticLockingException("Optimistic Locking Occurring!!!!!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int getAccountVersion(Trade trade, int securityId, Connection connection) {
        String query = "SELECT version FROM positions WHERE account_number = ? and security_id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trade.getAccountNumber());
            stmt.setInt(2, securityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("version");
            } else {
                // Return 0 if no account exists for the given credit card number
                return -1;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return 0;
    }

    public void updateJEForPositionsUpdate(Trade trade, Connection connection){
        String updateJEQuery = "update journal_entry set position_posted_status = ? where trade_id = ?";

        try(PreparedStatement psUpdateJe = connection.prepareStatement(updateJEQuery)){
            psUpdateJe.setString(1,"Posted");
            psUpdateJe.setString(2, trade.getTradeID());

            psUpdateJe.executeUpdate();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
