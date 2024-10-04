package io.reactivestax.repo;

import io.reactivestax.model.Trade;

import java.sql.*;

public class TradesDBRepo {

    public TradesDBRepo() {
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
                insert into journal_entry (account_number, security_id, direction, quantity, posted_status, trade_execution_time)
                values (?,?,?,?,?,?)
                """;

        try(PreparedStatement insertionQuery = connection.prepareStatement(writeToJournalQuery)){
            insertionQuery.setString(1,trade.getAccountNumber());
            insertionQuery.setInt(2,getSecurityIdForCusip(trade.getCusip(),connection));
            insertionQuery.setString(3,trade.getActivity());
            insertionQuery.setInt(4,trade.getQuantity());
            insertionQuery.setString(5,"Not Posted");
            insertionQuery.setTimestamp(6, new Timestamp(trade.getTransactionTime().getTime()));

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

    public void updatePositionsTable(Trade trade, Connection connection){
        int securityID = getSecurityIdForCusip(trade.getCusip(), connection);

        String smartInsertionAndUpdateQueryCredit = "Insert into positions (account_number, security_id, position) values (?,?,?) on duplicate key update position = (position - ?)";
        String smartInsertionAndUpdateQueryDebit = "Insert into positions (account_number, security_id, position) values (?,?,?) on duplicate key update position = (position + ?)";

        try(PreparedStatement psSmartQueryCredit  = connection.prepareStatement(smartInsertionAndUpdateQueryCredit);
        PreparedStatement psSmartQueryDebit = connection.prepareStatement(smartInsertionAndUpdateQueryDebit)){

            if(trade.getActivity().equals("BUY")){
                psSmartQueryDebit.setString(1,trade.getAccountNumber());
                psSmartQueryDebit.setInt(2, securityID);
                psSmartQueryDebit.setInt(3, trade.getQuantity());
                psSmartQueryDebit.setInt(4, trade.getQuantity());

                psSmartQueryDebit.executeUpdate();
            }else if(trade.getActivity().equals("SELL")){
                psSmartQueryCredit.setString(1,trade.getAccountNumber());
                psSmartQueryCredit.setInt(2, securityID);
                psSmartQueryCredit.setInt(3, -trade.getQuantity());
                psSmartQueryCredit.setInt(4, trade.getQuantity());

                psSmartQueryCredit.executeUpdate();
            } else {
                System.out.println("UnrecognisedActivityOperationException");
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

}
