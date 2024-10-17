package io.reactivestax.repo;

import io.reactivestax.entity.RawPayload;
import io.reactivestax.model.Trade;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PayloadDatabaseRepo {

    public void writeToDatabase(Connection connection, String tradeID, String status, String payload) {
        String query = "Insert into trades_payload (trade_id, status, payload, posted_status) values (?,?,?, 'Not Posted')";

        try (PreparedStatement psQuery = connection.prepareStatement(query)) {

            psQuery.setString(1, tradeID);
            psQuery.setString(2, status);
            psQuery.setString(3, payload);
            psQuery.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String readPayloadFromDB(Connection connection, String tradeID) {
        String query = "Select payload from trades_payload where trade_id=?";

        try (PreparedStatement psQuery = connection.prepareStatement(query)) {

            psQuery.setString(1, tradeID);
            ResultSet rsQuery = psQuery.executeQuery();
            rsQuery.next();
            return rsQuery.getString("payload");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void updateSecurityLookupStatus(Connection connection, Trade trade, String validityStatus) {
        String lookupUpdateQuery = "Update trades_payload set lookup_status = ? where trade_id = ?";

        try (PreparedStatement psLookupQuery = connection.prepareStatement(lookupUpdateQuery)) {

            psLookupQuery.setString(2, trade.getTradeID());
            if (validityStatus.equals("Valid")) {
                psLookupQuery.setString(1, "Succeeded");
            } else {
                psLookupQuery.setString(1, "Failed");
            }

            psLookupQuery.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateJournalEntryStatus(Connection connection, Trade trade) {
        String updateJEQuery = "Update trades_payload set posted_status = 'Posted' where trade_id = ?";

        try (PreparedStatement updateJEps = connection.prepareStatement(updateJEQuery)) {

            updateJEps.setString(1, trade.getTradeID());

            updateJEps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ------------ Hibernate --------------

    public void writeToDatabaseUsingHibernate(Session session, String tradeID, String payload, String status) {
        try {
            RawPayload rawPayload = new RawPayload();
            rawPayload.setTradeID(tradeID);
            rawPayload.setStatus(status);
            rawPayload.setPayload(payload);
            rawPayload.setLookupStatus("Non Posted");
            rawPayload.setPostedStatus("Non Posted");
            Transaction transaction;
            try {
                transaction = session.beginTransaction();
            } catch (Exception e) {
                transaction = session.getTransaction();
            }
            session.persist(rawPayload);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readPayloadFromDBUsingHibernate(Session hibernateSession, String tradeID) {

        CriteriaBuilder builder = hibernateSession.getCriteriaBuilder();
        CriteriaQuery<RawPayload> query = builder.createQuery(RawPayload.class);
        Root<RawPayload> root = query.from(RawPayload.class);
        query.select(root).where(builder.equal(root.get("tradeID"), tradeID));
        List<RawPayload> students = hibernateSession.createQuery(query).getResultList();

        if (students != null) return students.get(0).getPayload();
        else return null;
    }

    public void updateSecurityLookupStatusUsingHibernate(Session hibernateSession, Trade trade, String lookupStatus) {
        Transaction transaction;
        try {
            transaction = hibernateSession.beginTransaction();
        } catch (Exception e) {
            transaction = hibernateSession.getTransaction();
        }
        try {
            CriteriaBuilder builder = hibernateSession.getCriteriaBuilder();
            CriteriaUpdate<RawPayload> criteriaUpdate = builder.createCriteriaUpdate(RawPayload.class);
            Root<RawPayload> root = criteriaUpdate.from(RawPayload.class);
            if (lookupStatus.equals("Valid")) {
                criteriaUpdate.set(root.get("lookupStatus"), "Succeeded");
            } else {
                criteriaUpdate.set(root.get("lookupStatus"), "Failed");
            }
            criteriaUpdate.where(builder.equal(root.get("tradeID"), trade.getTradeID()));
            hibernateSession.createQuery(criteriaUpdate).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateJournalEntryStatusUsingHibernate(Session hibernateSession, Trade trade) {
        Transaction transaction;
        try {
            transaction = hibernateSession.beginTransaction();
        } catch (Exception e) {
            transaction = hibernateSession.getTransaction();
        }
        try {
            CriteriaBuilder builder = hibernateSession.getCriteriaBuilder();
            CriteriaUpdate<RawPayload> jeStatusUpdate = builder.createCriteriaUpdate(RawPayload.class);
            Root<RawPayload> root = jeStatusUpdate.from(RawPayload.class);
            jeStatusUpdate.set(root.get("postedStatus"), "Posted");
            jeStatusUpdate.where(builder.equal(root.get("tradeID"), trade.getTradeID()));

            hibernateSession.createQuery(jeStatusUpdate).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
