package io.reactivestax.repo;

import io.reactivestax.entity.JournalEntry;
import io.reactivestax.entity.Position;
import io.reactivestax.model.Trade;
import io.reactivestax.utility.exceptions.OptimisticLockingException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import java.sql.*;
import java.util.List;

public class TradesDBRepo {

    public TradesDBRepo() {
        //I don't really know what to enter here. I am trying to resolve sonar lint warning here...
    }

    public String checkIfValidCUSIP(Connection connection, Trade trade) {
        String lookupQuery = "select 1 from SecuritiesReferenceV2 where cusip = ?";

        try (PreparedStatement psLookUp = connection.prepareStatement(lookupQuery)) {

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

    public int getSecurityIdForCusip(Connection connection, String cusip) {

        String lookUpSecurityQuery = "Select security_id from SecuritiesReferenceV2 where cusip = ?";

        try (PreparedStatement ps = connection.prepareStatement(lookUpSecurityQuery)) {

            ps.setString(1, cusip);
            ResultSet resultSet = ps.executeQuery();
            resultSet.next();
            return resultSet.getInt("security_id");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    public void writeTradeToJournalTable(Connection connection, Trade trade) {
        String writeToJournalQuery = """
                insert into journal_entry (account_number, security_id, direction, quantity, position_posted_status, trade_execution_time, trade_id)
                values (?,?,?,?,?,?,?)
                """;

        try (PreparedStatement insertionQuery = connection.prepareStatement(writeToJournalQuery)) {
            insertionQuery.setString(1, trade.getAccountNumber());
            insertionQuery.setInt(2, getSecurityIdForCusip(connection, trade.getCusip()));
            insertionQuery.setString(3, trade.getActivity());
            insertionQuery.setInt(4, trade.getQuantity());
            insertionQuery.setString(5, "Not Posted");
            insertionQuery.setTimestamp(6, new Timestamp(trade.getTransactionTime().getTime()));
            insertionQuery.setString(7, trade.getTradeID());

            insertionQuery.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void updatePositionsTable(Connection connection, Trade trade) throws OptimisticLockingException {
        int securityID = getSecurityIdForCusip(connection, trade.getCusip());
        int version = getAccountVersion(connection, trade, securityID);

        String positionInsertQuery = "Insert into positions (account_number, security_id, position, version) values (?,?,?,0)";
        String positionUpdateQuery = "update positions set position = (position + ?), version = (version + 1) where version = ?";

        try (PreparedStatement psPositionInsertQuery = connection.prepareStatement(positionInsertQuery);
             PreparedStatement psPositionUpdateQuery = connection.prepareStatement(positionUpdateQuery)) {

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

    private int getAccountVersion(Connection connection, Trade trade, int securityId) {
        String query = "SELECT version FROM positions WHERE account_number = ? and security_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, trade.getAccountNumber());
            stmt.setInt(2, securityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("version");
            } else {
                // Return 0 if no account exists for the given credit card number
                return -1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    public void updateJEForPositionsUpdate(Connection connection, Trade trade) {
        String updateJEQuery = "update journal_entry set position_posted_status = ? where trade_id = ?";

        try (PreparedStatement psUpdateJe = connection.prepareStatement(updateJEQuery)) {
            psUpdateJe.setString(1, "Posted");
            psUpdateJe.setString(2, trade.getTradeID());

            psUpdateJe.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------ Hibernate --------------

    public void writeTradeToJournalTableUsingHibernate(Session hibernateSession, Connection sqlConnection, Trade trade) {
        try {
            JournalEntry journalEntry = new JournalEntry();
            journalEntry.setAccountNumber(trade.getAccountNumber());
            journalEntry.setActivity(trade.getActivity());
            journalEntry.setPositionPostedStatus("Non Posted");
            journalEntry.setQuantity(trade.getQuantity());
            journalEntry.setSecurityID(getSecurityIdForCusip(sqlConnection, trade.getCusip()));
            journalEntry.setTradeExecutionTime(trade.getTransactionTime());
            journalEntry.setTradeID(trade.getTradeID());

            hibernateSession.persist(journalEntry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getAccountVersionUsingHibernate(Session hibernateSession, Trade trade, int securityId) {
        CriteriaBuilder builder = hibernateSession.getCriteriaBuilder();
        CriteriaQuery<Position> getPositionVersionQuery = builder.createQuery(Position.class);
        Root<Position> root = getPositionVersionQuery.from(Position.class);
        getPositionVersionQuery.select(root).where(
                builder.and(
                        builder.equal(root.get("accountNumber"), trade.getAccountNumber()),
                        builder.equal(root.get("securityID"), securityId)
                ));
        List<Position> result = hibernateSession.createQuery(getPositionVersionQuery).getResultList();

        if (result.isEmpty()) {
            return -1;
        } else {
            return result.get(0).getVersion();
        }
    }

    public void updatePositionsTableUsingHibernate(Session hibernateSession, Connection sqlConnection, Trade trade) {
        try {
            int securityID = getSecurityIdForCusip(sqlConnection, trade.getCusip());
            int version = getAccountVersionUsingHibernate(hibernateSession, trade, securityID);

            if (version == -1) {
                //Perform Insertion Logic
                Position position = new Position();

                position.setAccountNumber(trade.getAccountNumber());
                position.setSecurityID(securityID);
                position.setVersion(0);
                if (trade.getActivity().equals("BUY")) {
                    position.setPositionAmount(trade.getQuantity());
                } else if (trade.getActivity().equals("SELL")) {
                    position.setPositionAmount(trade.getQuantity());
                } else System.out.println("UnrecognisedActivityOperationException");

                hibernateSession.persist(position);

            } else {
                //Perform Update Logic
                String updateHQL = "update Position p set p.positionAmount = (p.positionAmount + :positionIncrement), p.version = (p.version +1) where p.version = :currentVersion";
                Query updatePositionQuery = hibernateSession.createQuery(updateHQL);

                if (trade.getActivity().equals("BUY")) {
                    updatePositionQuery.setParameter("positionIncrement", trade.getQuantity());
                } else if (trade.getActivity().equals("SELL")) {
                    updatePositionQuery.setParameter("positionIncrement", -trade.getQuantity());
                } else System.out.println("UnrecognisedActivityOperationException...");

                updatePositionQuery.setParameter("currentVersion", version);

                if (updatePositionQuery.executeUpdate() == 0)
                    throw new OptimisticLockingException("Optimistic Locking Occurring!!!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateJEForPositionsUpdateUsingHibernate(Session hibernateSession, Trade trade) {
        try {
            CriteriaBuilder builder = hibernateSession.getCriteriaBuilder();
            CriteriaUpdate<JournalEntry> positionStatusUpdate = builder.createCriteriaUpdate(JournalEntry.class);
            Root<JournalEntry> root = positionStatusUpdate.from(JournalEntry.class);
            positionStatusUpdate.set(root.get("positionPostedStatus"), "Posted");
            positionStatusUpdate.where(builder.equal(root.get("tradeID"), trade.getTradeID()));

            hibernateSession.createQuery(positionStatusUpdate).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
